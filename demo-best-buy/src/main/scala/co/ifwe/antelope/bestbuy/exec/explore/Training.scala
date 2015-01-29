package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

import co.ifwe.antelope.TrainingExample
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.model.{BestBuyModel, SpellingModel}
import co.ifwe.antelope.bestbuy.{BestBuyScoringContext, UniformTrainingSampling}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter}
import co.ifwe.antelope.util.ProgressMeter

object Training extends ExploreApp with SimpleState {
  val ts = new UniformTrainingSampling

  var viewCt = 0
  var updateCt = 0
  val pm = new ProgressMeter(extraInfo = () => s"{views: $viewCt, updates: $updateCt}")
  val m = new BestBuyModel
  val sm = new SpellingModel
  val trainingWriter = new MultiFormatWriter(List((Config.trainingDir + File.separatorChar + "training_data.csv",
    new CsvTrainingFormatter(m.featureNames))))
  try {
    val TRAINING_START = Config.trainingStart
    val TRAINING_LIMIT = Config.trainingLimit
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
            val randomDoc = ts.getDoc()
            printTrainingResult(randomDoc, randomDoc == pv.skuSelected)
          }
        case pu: ProductUpdate =>
          ts.registerDoc(pu.sku)
          updateCt += 1
      }
      pm.increment()
      sm.update(e)
      m.update(e)
    }
  } finally {
    trainingWriter.close
  }
  pm.finished()
}
