package co.ifwe.antelope.bestbuy.event

import java.io.File

import co.ifwe.antelope.bestbuy.IOUtil._
import co.ifwe.antelope.io.{CachedEventConfiguration, CsvEventSourceProcessor, KryoEventStorage, XmlEventSourceProcessor}
import co.ifwe.antelope.{Event, EventHistory}

import scala.language.postfixOps

/**
 * Saved event history for the Best Buy data set. Merges both the product catalog,
 * as extracted from xml files, with the product views, stored in a training csv
 * file. All configuration is drawn through Env.
 */
class BestBuyEventHistory(viewsFn: String, productsDir: String, cacheDir: String) extends EventHistory {
  val ec = new CachedEventConfiguration {
    override def cacheDir(): String = BestBuyEventHistory.this.cacheDir
    override def storage(): KryoEventStorage = new KryoEventStorage {
      kryo.register(classOf[ProductView])
      kryo.register(classOf[ProductUpdate])
      kryo.register(classOf[Array[String]])
    }
  }
  ec.addEvents(new File(viewsFn).toURI.toURL,
    CsvEventSourceProcessor(e =>
      new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong))
  )
  val productFiles = findFiles(productsDir, _.toLowerCase.endsWith(".xml"))
  ec.addEvents(productFiles.map(_.toURI.toURL), XmlEventSourceProcessor[ProductUpdate]("product",
    node => {
      val ts = node \ "startDate" text
      val sku = node \ "sku" text
      val name = node \ "name" text
      val description = (node \ "longDescription" text).stripPrefix("Synopsis")
      val categories = (node \\ "category" \ "name").map(_.text).toArray
      new ProductUpdate(getProductDate(ts), sku.toLong, name, description, categories)
    }
  ))

  override def publishEvent(e: Event): Unit = {
    throw new RuntimeException("not implemented")
  }

  override def getEvents(startTime: Long, endTime: Long, eventFilter: (Event) => Boolean, eventHandler: (Event) => Boolean): Unit = {
    val it = ec.events.filter(e => e.ts >= startTime && e.ts < endTime).filter(eventFilter).iterator
    while (it.hasNext) {
      if (!eventHandler(it.next())) return
    }
  }
}
