package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.Env
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter, VowpalTrainingFormatter}
import co.ifwe.antelope.model.Training

/**
 * Generate training data - run this program to generate training data
 * for the model specified in [[Env]]
 */
object LearnedRankerTraining extends App with Env {
  val t = new Training[ProductSearchScoringContext, Model[ProductSearchScoringContext]] {
    override val eventHistory = LearnedRankerTraining.eventHistory
    override def newDocUpdated(e: Event): Option[Long] = {
      e match {
        case pu: ProductUpdate => Some(pu.sku)
        case _ => None
      }
    }

    override def getTrainingExamples(e: Event): Option[Iterable[TrainingExample]] = {
      e match {
        case pv: ProductView =>
          val ctx = new ProductSearchScoringContext {
            val t = pv.ts
            val query = pv.query
          }
          val randomDoc = allDocs.getRandomDoc(rnd)
          // We generate two training examples, one for the sku selected and a second for a sku selected at random
          Some(Array(
            new TrainingExample(true, model.featureNames zip model.featureValues(ctx, pv.skuSelected)),
            new TrainingExample(randomDoc == pv.skuSelected, model.featureNames zip model.featureValues(ctx, randomDoc))
          ))
        case _ => None
      }
    }
  }
  val trainingWriter = new MultiFormatWriter(Array(
    (getTrainingFile("training_data.csv"),
      new CsvTrainingFormatter(model.featureNames)),
    (getTrainingFile("training_data_vw.txt"),
      new VowpalTrainingFormatter())
  ))
  try {
    t.train(TRAINING_START,TRAINING_LIMIT, model, trainingWriter)
  } finally {
    trainingWriter.close()
  }
}
