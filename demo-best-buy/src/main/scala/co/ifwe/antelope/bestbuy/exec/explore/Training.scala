package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

import co.ifwe.antelope.TrainingExample
import co.ifwe.antelope.bestbuy.BestBuyScoringContext
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model.{SpellingModel, BestBuyModel}
import co.ifwe.antelope.io.{MultiFormatWriter, CsvTrainingFormatter}
import co.ifwe.antelope.util.ProgressMeter

import collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Training extends ExploreApp with SimpleState {
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

  val pm = new ProgressMeter()
  val m = new BestBuyModel
  val sm = new SpellingModel
  val trainingWriter = new MultiFormatWriter(List((FileLocations.trainingDir + File.separatorChar + "training_data.csv",
    new CsvTrainingFormatter(m.featureNames))))
  try {
    var viewCt = 0
    val TRAINING_START = 100000
    val TRAINING_LIMIT = 500000
    val it = events.iterator
    while (it.hasNext && viewCt < TRAINING_LIMIT) {
      val e = it.next
      e match {
        case pv: ProductView =>
          def printTrainingResult(docId: Long, outcome: Boolean): Unit = {
            val ctx = new BestBuyScoringContext(pv.query, sm, pv.ts)
            trainingWriter.write(new TrainingExample(outcome, m.featureNames zip m.featureValues(ctx, docId)))
          }
          viewCt += 1
          if (viewCt > TRAINING_START) {
            printTrainingResult(pv.skuSelected, true)
            val randomDoc = getRandomDoc()
            printTrainingResult(randomDoc, randomDoc == pv.skuSelected)
          }
        case pu: ProductUpdate =>
          registerDoc(pu.sku)
      }
      pm.increment()
      sm.update(e)
      m.update(e)
      viewCt < TRAINING_LIMIT
    }
  } finally {
    trainingWriter.close
  }
  pm.finished()
}
