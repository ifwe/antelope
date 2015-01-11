package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model._
import co.ifwe.antelope.util.ProgressMeter
import co.ifwe.antelope.{Event, EventProcessor}

import scala.collection.mutable

/**
 * Wrapper functionality for updating a model with event data.
 *
 * @param weights model parameters used during ranking
 * @param progressPrintInterval how many events to process between printing progress
 */
abstract class ModelEventProcessor(weights: Array[Double] = null, progressPrintInterval: Int = 5000) extends EventProcessor {
  val m = new BestBuyModel
  val allDocs = mutable.HashSet[Long]()
  private val progressMeter = new ProgressMeter(progressPrintInterval)

  def topDocs(query: String, n: Int): Array[Long] = {
    val docs = allDocs.toArray
    (docs zip m.score(new BestBuyScoringContext(query), docs, weights)).sortBy(-_._2).take(n).map(_._1)
  }

  override protected def consume(e: Event) = {
    // filter out duplicate product updates.  This keeps things simple, though
    // robust features should be able to deal with multiple updates on the same
    // product
    val skipUpdate = {
      e match {
        case pu: ProductUpdate => allDocs.contains(pu.sku)
        case _ => false
      }
    }
    e match {
      case pu: ProductUpdate =>
        allDocs += pu.sku
      case pv: ProductView =>
        progressMeter.increment
      case _ =>
    }
    if (!skipUpdate) {
      m.update(e)
    }
  }

  override protected def postProcess(): Unit = {
    progressMeter.finished()
    super.postProcess()
  }
}
