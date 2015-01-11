package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.bestbuy.{EventProcessing, RecommendationStats, ModelEventProcessor}
import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.event.ProductView

/**
 * Score a some of the documents based on training data - run this
 * program to test or demonstrate effectiveness of the model.
 */
object LearnedRankerScoring extends App with EventProcessing {
  override def productViewLimit(): Int = 40000
  override protected def getEventProcessor() = {
    val rs = new RecommendationStats()

    new ModelEventProcessor(
        weights = Array(88.77053,2437.086,0.1170896,7670.614,-0.02810121),
        progressPrintInterval = 500) {
      var viewCt = 0

      override protected def consume(e: Event) = {
        e match {
          case pv: ProductView =>
            viewCt += 1
            if (viewCt > 30000) {
              val td = topDocs(pv.query, 5)
              rs.record(pv.skuSelected, td)
            }
          case _ =>
        }
        super.consume(e)
      }
      override protected def postProcess() {
        println("%s finishing with stats: %s".format(m, rs))
        super.postProcess()
      }
    }
  }
}
