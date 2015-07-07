package co.ifwe.antelope.bestbuy

import java.io.File

import co.ifwe.antelope.EventHistory
import co.ifwe.antelope.bestbuy.event.BestBuyEventHistory
import co.ifwe.antelope.bestbuy.model.{BestBuyModel, DemoBestBuyModel, SpellCheckedModel}

import scala.util.Random

/**
 * Configuration parameters for the Best Buy Demo.
 *
 * You must set environment variables as follows:
 *
 *   - ANTELOPE_DATA is the directory containing downloaded training data files
 *   - ANTELOPE_TRAINING is the directory for writing training data
 *   - ANTELOPE_CACHE is the directory used to cache a binary version of parsed text inputs
 *
 */
trait Env {
  final val TRAINING_START = 1315615668858L // 10,000 events
  final val TRAINING_LIMIT = 1318703822781L // 30,000 events
  final val SCORING_LIMIT = 1319718816716L  // 40,000 events

  final val progressPrintInterval = 1000

  val dataDir = System.getenv("ANTELOPE_DATA")
  if (dataDir == null || dataDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_DATA environment variable")
  }
  val trainingDir = System.getenv("ANTELOPE_TRAINING")
  if (trainingDir == null || trainingDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_TRAINING environment variable")
  }

  val cacheDir = System.getenv("ANTELOPE_CACHE")
  if (cacheDir == null) {
    throw new IllegalArgumentException("must set $ANTELOPE_CACHE environment variable")
  }

  val viewsFn = dataDir + File.separator + "train_sorted.csv"
  val productsDir = dataDir + File.separator + "product_data" + File.separator + "products"
  val weightsFn = trainingDir + File.separator + "r_logit_coef.txt"

  protected def getTrainingFile(name: String) = trainingDir + File.separator + name

  val rnd = new Random(23049L)
  val eventHistory: EventHistory = new BestBuyEventHistory(viewsFn, productsDir, cacheDir)

//  val model = new DemoBestBuyModel
//  val model = new BestBuyModel
  val model = new SpellCheckedModel(new BestBuyModel)
}
