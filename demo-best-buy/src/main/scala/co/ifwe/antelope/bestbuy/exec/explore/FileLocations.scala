package co.ifwe.antelope.bestbuy.exec.explore

import java.io.File

object FileLocations {
  val trainingDir = "/Users/johann/dev/aml/bestbuy/training_large"
  val viewsFn = "/Users/johann/dev/aml/bestbuy/data_large/train_sorted.csv"
  val viewsFnBin = viewsFn + ".bin"
  val productsDirectory = "/Users/johann/dev/aml/bestbuy/data_large/product_data/products"
  val mergedProducts = productsDirectory + File.separatorChar + "products_merged.bin"
  val allEvents = "/Users/johann/dev/aml/bestbuy/data_large/all_events.bin"
}
