package co.ifwe.antelope.bestbuy.exec

import java.io.File
import java.util.{Calendar, GregorianCalendar, TimeZone}

import co.ifwe.antelope.bestbuy.event.{ProductView, ProductUpdate}
import co.ifwe.antelope.{IterableUpdateDefinition, Event, EventProcessor, State}
import co.ifwe.antelope.bestbuy.{IOUtil, EventProcessing}

import scala.collection.mutable

object ExploreData extends App with EventProcessing {
//  override def productViewLimit() = 20
  override protected def getEventProcessor() = new EventProcessor {
    val s = new State

    def genIterableUpdateDefinition[T](f: PartialFunction[Event, T]): IterableUpdateDefinition[T] = {
      new IterableUpdateDefinition[T] {
        override def getFunction: PartialFunction[Event, Iterable[T]] = new PartialFunction[Event, Iterable[T]] {
          override def isDefinedAt(x: Event): Boolean = f.isDefinedAt(x)
          override def apply(e: Event): Iterable[T] = List(f.apply(e))
        }
      }
    }

    def genIterableUpdateDefinitionIt[T](f: PartialFunction[Event, Iterable[T]]): IterableUpdateDefinition[T] = {
      new IterableUpdateDefinition[T] {
        override def getFunction: PartialFunction[Event, Iterable[T]] = new PartialFunction[Event, Iterable[T]] {
          override def isDefinedAt(x: Event): Boolean = f.isDefinedAt(x)
          override def apply(e: Event): Iterable[T] = f.apply(e)
        }
      }
    }

  // Define an interface that is easier to read
  def counter[T](f: PartialFunction[Event, T]) = {
    s.counter(genIterableUpdateDefinition(f))
  }
  def set[T](f: PartialFunction[Event, T]) = {
    s.set(genIterableUpdateDefinition(f))
  }
  def sum[T](f: PartialFunction[Event, (T,Int)]) = {
    s.sum(genIterableUpdateDefinition(f))
  }
  def sum2(f: PartialFunction[Event, Int]) = {
    s.sum(genIterableUpdateDefinition(f))
  }

  val productCatalog = mutable.HashMap[Long, ProductUpdate]()

  type EventCapture[T] = PartialFunction[Event, T]

  // get the total number of product updates
  val update: EventCapture[Int] = { case _: ProductUpdate => 1 }
  val allUpdates = counter { update }
  val allViews = counter { case _: ProductView => 1 }

  val usersViews = set { case pv: ProductView => pv.user }
  val usersViewCounts = counter { case pv: ProductView => pv.user }

  // Time to click
  val timeToView = sum2 { case pv: ProductView => (pv.ts - pv.queryTs).toInt }

  // Time to click by previous clicks
  // Note that the dependency here should influence the order of update application
  val xTimeToViewCt = counter { case pv: ProductView => usersViewCounts(pv.user) }
  val xTimeToView = sum { case pv: ProductView => usersViewCounts(pv.user) -> (pv.ts - pv.queryTs).toInt }

  // What categories are in the catalog
  val allCategories = s.counter(genIterableUpdateDefinitionIt{ case pu: ProductUpdate => pu.categories })
  // What categories are people searching for
  val searchedCategories = s.counter(genIterableUpdateDefinitionIt{
    case pv: ProductView => productCatalog.get(pv.skuSelected) match {
      case Some(pu) => pu.categories
      case None => Nil
    }
  })

  val searchedProductCategories = s.set(genIterableUpdateDefinitionIt {
    case pv: ProductView => productCatalog.get(pv.skuSelected) match {
        case Some(pu) => pu.categories
        case None => Nil
      }
    },
    genIterableUpdateDefinition {
      case pv: ProductView => pv.skuSelected
    }
  )

  val searchesByTimeOfDay = counter {
    case pv: ProductView => {
      val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
      cal.setTimeInMillis(pv.ts)
      cal.get(Calendar.HOUR_OF_DAY)
    }
  }

  val searchesByDay = counter {
    case pv: ProductView => {
      val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
      cal.setTimeInMillis(pv.ts)
      "%04d%02d%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }
  }

  object DayOfWeek extends Enumeration {
    type WeekDay = Value
    val Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday = Value
  }

  val searchesByDayOfWeek = counter {
    case pv: ProductView => {
      val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
      cal.setTimeInMillis(pv.ts)
      DayOfWeek(cal.get(Calendar.DAY_OF_WEEK) - 1)
    }
  }

  val popularityBySku = counter { case pv: ProductView => pv.skuSelected }

  val popularityByDay = s.counter(
    genIterableUpdateDefinition{ case pv: ProductView => pv.skuSelected },
    genIterableUpdateDefinition{
      case pv: ProductView => {
        val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
        cal.setTimeInMillis(pv.ts)
        "%04d%02d%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
      }
    }
  )

  override protected def consume(e: Event): Unit = {
      e match {
        case pu: ProductUpdate => productCatalog += pu.sku -> pu
        case _ =>
      }
      s.update(List(e))
    }
    override protected def postProcess(): Unit = {
      println(s"updates: ${allUpdates()}, views: ${allViews()}")
      println(s"number of users issuing queries ${usersViews.size}")
      usersViewCounts.toMap.toArray.sortBy(-_._2).take(10).foreach(println)
      // Number of views by user count
      usersViewCounts.toMap.groupBy(_._2).mapValues(_.size).toArray.sortBy(-_._1).foreach(println)

      // total time to click
      println(s"total time to click ${timeToView()}")
      println(s"average time to click ${timeToView()/allViews()/1000D}s")

      // Time to click by previous clicks
      println("time to click by number of clicks done previously")
      val times = xTimeToView.toMap
      val cts = xTimeToViewCt.toMap
      times.map{ case (k, v) => (k, cts(k), v / cts(k)/1000D)}.toArray.sortBy(_._1).foreach(println)

      // Describe the categories
      println("categories")
      val sc = searchedCategories.toMap
      val categoryProducts = searchedProductCategories.mapSize()
      allCategories.toMap.foreach {
        case (category, products) =>
          val searches = sc(category)
          val searchedProducts = categoryProducts(category)
          println(s"$category $products $searchedProducts $searches")
      }

      println("searches by time of day")
      searchesByTimeOfDay.toMap.toArray.sortBy(_._1).foreach(println)

      println("searches by day")
      searchesByDay.toMap.toArray.sortBy(_._1).foreach(println)

      println(s"confirm order of days in week: ${0 until 7 map {DayOfWeek(_)} mkString(",")}")
      println("searches by day of week")
      searchesByDayOfWeek.toMap.toArray.sortBy(_._1).foreach(println)

      // Top product popularity charts
      val topSkus = popularityBySku.toMap.toArray.sortBy(-_._2).take(10)
      for ((sku, ct) <- topSkus) {
        val title = productCatalog(sku).name
        println(s"$title | $sku | $ct")
//        popularityByDay.mapAt(sku).toArray.sortBy(_._1).foreach(println)
      }
      IOUtil.csvOutput(trainingDir + File.separator + "popularity_trends.csv",
        List("title","date","ct"), w =>
          for ((sku, ct) <- topSkus) {
            val title = productCatalog(sku).name
            popularityByDay.mapAt(sku).toArray.sortBy(_._1).foreach(x => w.write(List(title, x._1.toString, x._2.toString)))
          }
      )
    }
  }
}
