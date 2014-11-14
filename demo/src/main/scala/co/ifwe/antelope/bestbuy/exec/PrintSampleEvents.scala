package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.bestbuy.EventProcessing
import co.ifwe.antelope.{Event, EventProcessor}
import co.ifwe.antelope.bestbuy.event._

/**
 * Demo functionality, just show a few events so that we can see what they look like
 */
object PrintSampleEvents extends App with EventProcessing {

  override def productViewLimit() = 10
  override def productUpdateLimit() = 10

  override protected def getEventProcessor(): EventProcessor = new EventProcessor {
    override protected def consume(e: Event): Unit = {
      e match {
        case pu : ProductUpdate => println(pu)
        case pv : ProductView => println(pv)
      }
    }
  }
}
