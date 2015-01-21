package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, TimeZone, GregorianCalendar}

import co.ifwe.antelope.bestbuy.event.ProductView

object ByHour extends ExploreApp {
  val cts = new Array[Int](24)

  val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))

  events.foreach { pv: ProductView =>
    cal.setTimeInMillis(pv.ts)
    cts(cal.get(Calendar.HOUR_OF_DAY)) += 1
  }

  println(cts)

  printTiming()
}
