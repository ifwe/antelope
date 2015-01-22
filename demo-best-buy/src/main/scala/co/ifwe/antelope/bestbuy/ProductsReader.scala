package co.ifwe.antelope.bestbuy

import java.io.{FilenameFilter, File}
import java.net.URL

import co.ifwe.antelope.bestbuy.event.ProductUpdate

import scala.language.postfixOps
import scala.xml.XML

/**
 * Load Best Buy product catalog from an XML file, format
 * as in the
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 */
object ProductsReader {
  def read(fn: URL): Iterable[ProductUpdate] = {
    val catalog = XML.load(fn)
    for (x <- catalog \\ "product") yield {
      val ts = (x \ "activeUpdateDate" text)
      val sku = (x \ "sku" text)
      val name = (x \ "name" text)
      val description = (x \ "longDescription" text)
      val categories = (x \\ "category" \ "name").map(_.text).toArray
      new ProductUpdate(IOUtil.getProductTime(ts), sku.toLong, name, description, categories)
    }
  }

  def fromFile(fn: String): Iterable[ProductUpdate] = {
    read(new File(fn).toURI.toURL)
  }

  def fromDirectory(path: String): Iterable[ProductUpdate] = {
    (new File(path)).listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = {
        name.toLowerCase().endsWith(".xml")
      }
    }).toList.map(x => fromFile(x.getPath)).flatten
  }
}
