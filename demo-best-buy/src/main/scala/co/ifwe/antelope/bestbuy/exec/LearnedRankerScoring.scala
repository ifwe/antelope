package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.exec.LearnedRankerTraining.TRAINING_LIMIT
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
        weights = Array(128.6117,14.65755,-49.97339,202.6852,-203.251,604.0303,0.1090175,5564.74,-0.01820427,1.412504,0.09182059),
        progressPrintInterval = 500) {
      var viewCt = 0

      override protected def consume(e: Event) = {
        e match {
          case pv: ProductView =>
            viewCt += 1
            if (viewCt > TRAINING_LIMIT) {
              val td = topDocs(pv.query, pv.ts, 5).topDocs
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
