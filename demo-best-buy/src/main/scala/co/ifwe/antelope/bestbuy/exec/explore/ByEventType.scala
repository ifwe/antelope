package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, GregorianCalendar, TimeZone}

import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}

object ByEventType extends ExploreApp {
  var pvCt = 0
  var puCt = 0
  var outOfOrderCt = 0

  var lastTs = Long.MinValue
  events.foreach {
    case pv: ProductView =>
      pvCt += 1
      if (pv.ts < lastTs)
        outOfOrderCt += 1
      lastTs = pv.ts
    case pu: ProductUpdate =>
      puCt += 1
      if (pu.ts < lastTs)
        outOfOrderCt += 1
      lastTs = pu.ts
  }

  println(s"have $pvCt product views and $puCt product updates")
  println(s"have $outOfOrderCt out of order events from total of ${pvCt+puCt}")
  printTiming()
}
