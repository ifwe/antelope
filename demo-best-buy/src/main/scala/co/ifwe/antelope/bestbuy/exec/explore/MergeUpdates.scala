package co.ifwe.antelope.bestbuy.exec.explore

import co.ifwe.antelope.bestbuy.IOUtil
import co.ifwe.antelope.bestbuy.exec.explore.FileLocations._

object MergeUpdates extends App {
  val productFiles = IOUtil.findFiles(productsDirectory, _.toLowerCase.endsWith(".xml.bin"))
  val mergeCt = Merge.merge(productFiles, mergedProducts)

  println(s"merged $mergeCt")
}
