package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.bestbuy.event.ProductUpdate
import co.ifwe.antelope.model.Stats
import co.ifwe.antelope.Event

class BestBuyStats extends Stats[BestBuyEvaluation] {
  val ma = new MissAnalysis
  val rs = new RecommendationStats()

  override def record(evaluation: BestBuyEvaluation): Unit = {
    val hit = rs.record(evaluation.pv.skuSelected, evaluation.topDocs)
    if (!hit) {
      ma.miss(evaluation.pv, evaluation.topDocs)
    }
  }

  def update(e: Event) = {
    e match {
      case pu: ProductUpdate =>
        ma.register(pu)
      case _ =>
    }
  }

  def summarize(): Unit = {
    println("Miss Analysis")
    println(ma.summarize())
    println("Overall success rate: %s".format(rs))
  }
}
