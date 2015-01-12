package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.{EventProcessing, MissAnalysis, ModelEventProcessor, RecommendationStats}

/**
 * Score a some of the documents based on training data - run this
 * program to test or demonstrate effectiveness of the model.
 */
object LearnedRankerScoring extends App with EventProcessing {
  override def productViewLimit(): Int = 40000
  override protected def getEventProcessor() = {
    val rs = new RecommendationStats()
    val ma = new MissAnalysis()

    new ModelEventProcessor(
//        TODO - separate this for purpose of simple demo
//        weights = Array(87.48098,3013.327,0.1203471,4506.025,-0.02656143),
        weights = Array(89.24148,2222.281,0.1103307,4159.622,-0.0142663,1.403137,0.0668691),
        progressPrintInterval = 500) {
      var viewCt = 0

      override protected def consume(e: Event) = {
        e match {
          case pv: ProductView =>
            viewCt += 1
            if (viewCt > 30000) {
              val td = topDocs(pv.query, 5).topDocs
              val hit = rs.record(pv.skuSelected, td)
              if (!hit) {
                ma.miss(pv, td)
              }
            }
          case pu: ProductUpdate =>
            ma.register(pu)
          case _ =>
        }
        super.consume(e)
      }
      override protected def postProcess() {
        println("%s finishing with stats: %s".format(m, rs))
        println(ma.summarize())
        super.postProcess()
      }
    }
  }
}
