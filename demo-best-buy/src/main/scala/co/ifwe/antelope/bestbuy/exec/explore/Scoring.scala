package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model.{BestBuyModel, SpellingModel}
import co.ifwe.antelope.bestbuy.{BestBuyScoringContext, MissAnalysis, RecommendationStats, TopDocsResult}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter}
import co.ifwe.antelope.util.ProgressMeter

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Scoring extends ExploreApp with SimpleState {
  val allDocsSet = new mutable.HashSet[Long]()
  val allDocsArray = new ArrayBuffer[Long]()
  val rnd = new Random(23049L)
  private def registerDoc(docId: Long): Unit = {
    if (!allDocsSet.contains(docId)) {
      allDocsSet += docId
      allDocsArray += docId
    }
  }
  private def getRandomDoc(): Long = {
    allDocsArray(rnd.nextInt(allDocsArray.length))
  }

  val weights = Array(52.77964D,39.74343D,0.08017773D,-0.01137886D,0.8648198D,0.08873818D)

  val pm = new ProgressMeter()
  val m = new BestBuyModel
  val sm = new SpellingModel
  val rs = new RecommendationStats()
  val ma = new MissAnalysis()
  val trainingWriter = new MultiFormatWriter(List((Config.trainingDir + File.separatorChar + "training_data.csv",
    new CsvTrainingFormatter(m.featureNames))))
  try {
    var viewCt = 0
    val TRAINING_LIMIT = Config.trainingLimit
    val SCORING_LIMIT = Config.scoringLimit
    val it = events.iterator
    while (it.hasNext && viewCt < SCORING_LIMIT) {
      val e = it.next
      e match {
        case pv: ProductView =>
          viewCt += 1
          if (viewCt > TRAINING_LIMIT) {
            val scoringContext = new BestBuyScoringContext(pv.query, sm, pv.ts)
            // TODO arrayBuffer to array creates copy
            val docs = allDocsArray.toArray
            val n = 5
            val topDocs = (docs zip m.score(scoringContext, docs, weights)).sortBy(-_._2).take(n).map(_._1)
            val td = new TopDocsResult(pv.query, scoringContext.correction, topDocs, n).topDocs
            val hit = rs.record(pv.skuSelected, td)
            if (!hit) {
              ma.miss(pv, td)
            }
          }
        case pu: ProductUpdate =>
          registerDoc(pu.sku)
          ma.register(pu)
      }
      pm.increment()
      sm.update(e)
      m.update(e)
    }
  } finally {
    trainingWriter.close
  }
  pm.finished()
  println(ma.summarize())
  println("%s finishing with stats: %s".format(m, rs))
}
