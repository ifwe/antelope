package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.bestbuy.event.{ProductView, ProductUpdate}
import co.ifwe.antelope.{IterableUpdateDefinition, Event, EventProcessor, State}
import co.ifwe.antelope.bestbuy.EventProcessing

object ExploreData extends App with EventProcessing {
  override protected def getEventProcessor() = new EventProcessor {
    val s = new State
    import s._
    // get the total number of product updates
    val allUpdates = counter(new IterableUpdateDefinition[Int] {
      override def getFunction = {
        case _: ProductUpdate => List(1)
      }
    })
    val allViews = counter(new IterableUpdateDefinition[Int] {
      override def getFunction = {
        case _: ProductView => List(1)
      }
    })

    override protected def consume(e: Event): Unit = {
      s.update(List(e))
    }
    override protected def postProcess(): Unit = {
      println(s"updates: ${allUpdates()}, views: ${allViews()}")
    }
  }
}
