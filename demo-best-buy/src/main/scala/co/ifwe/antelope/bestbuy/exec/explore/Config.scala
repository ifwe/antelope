package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

object Config {
  private def getEnv(envVar: String): String = {
    val res = System.getenv(envVar)
    if (res == null || res.isEmpty) {
      throw new IllegalArgumentException(s"must set $$$envVar environment variable")
    }
    res
  }

  val dataDir = getEnv("ANTELOPE_DATA")
  val trainingDir = getEnv("ANTELOPE_TRAINING")
  val cacheDir = getEnv("ANTELOPE_CACHE")
  val trainingStart = getEnv("ANTELOPE_TRAINING_START").toLong
  val trainingLimit = getEnv("ANTELOPE_TRAINING_LIMIT").toLong
  val scoringLimit = getEnv("ANTELOPE_SCORING_LIMIT").toLong

  val viewsFn = dataDir + File.separator + "train_sorted.csv"
  val viewsFnBin = viewsFn + ".bin"
  val viewsFnBinCprog = viewsFn + ".bin-cp"
  val productsDirectory = dataDir + File.separator + "product_data/products"
  val mergedProducts = productsDirectory + File.separatorChar + "products_merged.bin"
  val allEvents = dataDir + File.separator + "all_events.bin"
}
