package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.Env
import co.ifwe.antelope.bestbuy.event.ProductView

/**
 * Print event once for every 10,000 views. The timestamps generated here are
 * used to inform training and evaluation timestamps.
 */
object PrintIntervalEvents extends App with Env {
  var pvCt = 0L

  eventHistory.getEvents(Long.MinValue, Long.MaxValue, _ => true, (e: Event) => {
    e match {
      case pv: ProductView =>
        if (pvCt % 10000 == 0) {
          println(s"$pvCt : ${pv.ts} : $pv")
        }
       pvCt += 1
      case _ =>
    }
    true
  })
}
