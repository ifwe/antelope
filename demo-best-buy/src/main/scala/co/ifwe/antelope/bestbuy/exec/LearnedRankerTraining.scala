package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.bestbuy.{BestBuyScoringContext, EventProcessing, ModelEventProcessor}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter, VowpalTrainingFormatter}

import scala.util.Random

/**
 * Generate training data - run this program to generate training data
 * for the model specified in [[ModelEventProcessor]]
 */
object LearnedRankerTraining extends App with EventProcessing {
  final val TRAINING_START = 10000
  final val TRAINING_LIMIT = 30000

  // Use only a portion of the data set so that we can use
  // the later time period for model evaluation
  override def productViewLimit(): Int = TRAINING_LIMIT

  override protected def getEventProcessor() = new ModelEventProcessor() {
    var trainingWriter: MultiFormatWriter = null
    val rnd = new Random(23049L)
    var viewCt = 0

    private def getRandomDoc() = {
      // TODO this is wasteful in making lots of copies
      val docs = allDocs.toArray
      docs(rnd.nextInt(docs.length))
    }

    override protected def init(): Unit = {
      super.init()
      trainingWriter = new MultiFormatWriter(Array(
        (getTrainingFile("training_data.csv"),
          new CsvTrainingFormatter(m.featureNames)),
        (getTrainingFile("training_data_vw.txt"),
          new VowpalTrainingFormatter())
      ))
    }

    override def consume(e: Event): Unit = {
      e match {
        case pv: ProductView =>
          def printTrainingResult(docId: Long, outcome: Boolean): Unit = {
            val ctx = new BestBuyScoringContext(pv.query, sm, pv.ts)
            trainingWriter.write(new TrainingExample(outcome, m.featureNames zip m.featureValues(ctx, docId)))
          }

          viewCt += 1
          if (viewCt > TRAINING_START) {
            // Generate training data
            printTrainingResult(pv.skuSelected, true)
            val randomDoc = getRandomDoc()
            printTrainingResult(randomDoc, randomDoc == pv.skuSelected)
          }
        case _ =>
      }
      super.consume(e)
    }

    override def onShutdown() {
      try {
        trainingWriter.close()
      } finally {
        super.onShutdown()
      }
    }
  }
}
