package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File
import java.net.URL

import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model.{BestBuyModel, SpellingModel}
import co.ifwe.antelope.bestbuy.{BestBuyScoringContext, MissAnalysis, RecommendationStats, TopDocsResult}
import co.ifwe.antelope.io.{WeightsReader, CsvTrainingFormatter, MultiFormatWriter}
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

  val weights = WeightsReader.getWeights(new File(Config.weightsFn).toURI.toURL)
  println(s"scoring using wights (${weights.mkString(",")})")

  var viewCt = 0
  var updateCt = 0
  val pm = new ProgressMeter(extraInfo = () => s"{views: $viewCt, updates: $updateCt}")
  val m = new BestBuyModel
  val sm = new SpellingModel
  val rs = new RecommendationStats()
  val ma = new MissAnalysis(100,100)
  val trainingWriter = new MultiFormatWriter(List((Config.trainingDir + File.separatorChar + "training_data.csv",
    new CsvTrainingFormatter(m.featureNames))))
  try {
    val TRAINING_LIMIT = Config.trainingLimit
    val SCORING_LIMIT = Config.scoringLimit
    val it = events.iterator
    while (it.hasNext && viewCt < SCORING_LIMIT) {
      val e = it.next
      e match {
        case pv: ProductView =>
          viewCt += 1
          if (viewCt > TRAINING_LIMIT) {
            val startTime = System.currentTimeMillis()
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
            if (Config.scoringTiming) {
              val elapsedTime = startTime - System.currentTimeMillis()
              println(s"""score in $elapsedTime ms for query "${pv.query}" with hit: $hit""")
            }
          }
        case pu: ProductUpdate =>
          updateCt += 1
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
