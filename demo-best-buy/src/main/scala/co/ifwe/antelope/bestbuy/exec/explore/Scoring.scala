package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

import co.ifwe.antelope.TrainingExample
import co.ifwe.antelope.bestbuy.{TopDocsResult, MissAnalysis, RecommendationStats, BestBuyScoringContext}
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model.{BestBuyModel, SpellingModel}
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

  val weights = Array(936626.9D,0D,0.02615726D,0.02090764D,0.02802838D,0.03497765D)

  val pm = new ProgressMeter()
  val m = new BestBuyModel
  val sm = new SpellingModel
  val rs = new RecommendationStats()
  val ma = new MissAnalysis()
  val trainingWriter = new MultiFormatWriter(List((FileLocations.trainingDir + File.separatorChar + "training_data.csv",
    new CsvTrainingFormatter(m.featureNames))))
  try {
    var viewCt = 0
    val TRAINING_START = 100000
    val TRAINING_LIMIT = 500000
    val SCORING_LIMIT = 1000000
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
}
