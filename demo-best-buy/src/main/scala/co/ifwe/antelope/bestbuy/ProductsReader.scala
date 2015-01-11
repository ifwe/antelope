package co.ifwe.antelope.bestbuy

import java.io.File
import java.net.URL

import scala.language.postfixOps
import scala.xml.XML

/**
 * Load Best Buy product catalog from an XML file, format
 * as in the
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 */
object ProductsReader {
  def read(fn: URL): Traversable[Product] = {
    val catalog = XML.load(fn)
    for (x <- catalog \\ "product") yield {
      val sku = (x \ "sku" text)
      val name = (x \ "name" text)
      val description = (x \ "longDescription" text)
      new Product(sku.toLong, name, description)
    }
  }

  def fromFile(fn: String): Traversable[Product] = {
    read(new File(fn).toURI.toURL)
  }
}
