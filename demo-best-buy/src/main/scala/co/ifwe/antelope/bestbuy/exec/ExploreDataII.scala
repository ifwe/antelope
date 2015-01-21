package co.ifwe.antelope.bestbuy.exec

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone, GregorianCalendar}

import co.ifwe.antelope.bestbuy.event.{Storage, ProductView}
import co.ifwe.antelope.util.ProgressMeter
import co.ifwe.antelope._

object ExploreDataII extends App {
  // Set up event sources
//  val createTime = System.currentTimeMillis()
  var eventCt = 0L

  val viewsFn = "/Users/johann/dev/aml/bestbuy/data_large/train_sorted.csv"
  val viewsFnBin = viewsFn + ".bin"

  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  val backupDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  def getTime(timeStr: String): Long = {
    try {
      df.parse(timeStr).getTime
    } catch {
      case e: java.text.ParseException => backupDf.parse(timeStr).getTime
    }
  }

  def getUser(userStr: String): Long = {
    java.lang.Long.parseUnsignedLong(userStr.substring(0, 16), 16) & 0x7fffffffffffffffL
  }

  val startTime = System.currentTimeMillis()
  println(s"starting tests at ${System.currentTimeMillis() - startTime}")
  val pm = new ProgressMeter(printInterval = 50000)

//  val xx = EventSource.fromFile(viewsFn).view.map(e =>
//    new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong))
//  ProductViewStorage.writeEvents(viewsFnBin, xx)
  val xx = Storage.readEvents(viewsFnBin)

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

  def counter[T](f: PartialFunction[Event, T]) = {
    s.counter(genIterableUpdateDefinition(f))
  }

  val searchesByTimeOfDay = counter {
    case pv: ProductView => {
      val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
      cal.setTimeInMillis(pv.ts)
      cal.get(Calendar.HOUR_OF_DAY)
    }
  }

  xx.foreach { pv: ProductView =>
    s.update(Array(pv))
    pm.increment()
  }

  println("searches by time of day")
  searchesByTimeOfDay.toMap.toArray.sortBy(_._1).foreach(println)

  pm.finished()

  //  override protected def getEventProcessor(): EventProcessor = new EventProcessor {
//    val pm = new ProgressMeter()
//    override def init(): Unit = {
//    }
//    override protected def consume(e: Event): Unit = {
//      pm.increment()
//    }
//    override def postProcess(): Unit = {
//      pm.finished()
//    }
//  }

}
