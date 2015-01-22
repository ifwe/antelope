package co.ifwe.antelope.bestbuy.exec.explore

import co.ifwe.antelope.bestbuy.IOUtil
import co.ifwe.antelope.bestbuy.event.Storage

object PrintUpdates extends App {
  val productsDirectory = "/Users/johann/dev/aml/bestbuy/data_large/product_data/products"

  val productFiles = IOUtil.findFiles(productsDirectory, _.toLowerCase.endsWith(".xml.bin"))

  for (fn <- productFiles) {
    println(fn)
    Storage.readEvents(fn).take(10).foreach(println)
  }
}
