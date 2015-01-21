package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, GregorianCalendar, TimeZone}

import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.{Event, IterableUpdateDefinition, State}

import scala.collection.mutable

object ByHourInHash extends ExploreApp {
  val cts = new mutable.HashMap[Int, Long]

  val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))

  events.foreach { pv: ProductView =>
    cal.setTimeInMillis(pv.ts)
    val hr = cal.get(Calendar.HOUR_OF_DAY)
    cts.put(hr, cts.getOrElse(hr, 0))
  }

  println("searches by time of day")
  cts.toMap.toArray.sortBy(_._1).foreach(println)

  printTiming()
 }
