package co.ifwe.antelope.bestbuy.exec.explore

import java.io.{FilenameFilter, File}

import co.ifwe.antelope.bestbuy.ProductsReader

object PrepLarge extends App {
  var ct = 0

  val productsDirectory = "/Users/johann/dev/aml/bestbuy/data_large/product_data/products"
  val productFiles =
    (new File(productsDirectory)).listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = {
        name.toLowerCase().endsWith(".xml")
      }
    }).map(_.getPath)

  def getMb(size: Long) = {
    (size / 1000) / 1000D
  }

  for (fn <- productFiles.take(10)) {
    val f = new File(fn)
    val len = f.length()
    println(s"open ${f.getName} with size ${getMb(len)} mb")
    val startTime = System.currentTimeMillis()
    val products = ProductsReader.fromFile(fn)
    println(s"read products at ${System.currentTimeMillis() - startTime}")
    println(s"number of products: ${products.size}")
    println(s"counted products at ${System.currentTimeMillis() - startTime}")
  }
}
