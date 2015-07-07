package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.event._
import co.ifwe.antelope.bestbuy.Env

/**
 * Demo functionality, just show a few events so that we can see what they look like
 */
object PrintSampleEvents extends App with Env {
  val pvCtMax = 10
  val puCtMax = 10
  var pvCt = 0
  var puCt = 0

  eventHistory.getEvents(Long.MinValue, Long.MaxValue, _ => true, (e: Event) => {
    e match {
      case pu: ProductUpdate =>
        if (puCt < pvCtMax) {
          println(pu)
          puCt += 1
        }
      case pv: ProductView =>
        if (pvCt < pvCtMax) {
          println(pv)
          pvCt += 1
        }
    }
    pvCt < pvCtMax || puCt < puCtMax
  })
}
