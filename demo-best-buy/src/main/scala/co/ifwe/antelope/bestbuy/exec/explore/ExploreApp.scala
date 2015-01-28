package co.ifwe.antelope.bestbuy.exec.explore

import java.io.{File, FileWriter, FilenameFilter, PrintWriter}

import co.ifwe.antelope.bestbuy.IOUtil
import co.ifwe.antelope.bestbuy.IOUtil._
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.bestbuy.exec.explore.state.Counter
import co.ifwe.antelope.io.{CachedEventConfiguration, CsvEventSourceProcessor, KryoEventStorage, XmlEventSourceProcessor}
import co.ifwe.antelope.util.ProgressMeter

import scala.language.postfixOps
import scala.xml.Node

trait ExploreApp extends App with CachedEventConfiguration {
  private val startTime = System.currentTimeMillis()
  private val pm = new ProgressMeter(printInterval = 50000)
  override val cacheDir = FileLocations.cacheDir
  println(s"starting tests at ${System.currentTimeMillis() - startTime}")

  override def storage = new KryoEventStorage {
    kryo.register(classOf[ProductView])
    kryo.register(classOf[ProductUpdate])
    kryo.register(classOf[Array[String]])
  }
  
  addEvents(new File(FileLocations.viewsFn).toURI.toURL, new CsvEventSourceProcessor[ProductView] {
    override def getEvent(fields: Map[String, String]): ProductView = {
      new ProductView(getTime(fields("click_time")), getTime(fields("query_time")),
        getUser(fields("user")), fields("query"), fields("sku").toLong)
    }
  })

  val productFiles =
    (new File(FileLocations.productsDirectory)).listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = {
        name.toLowerCase().endsWith(".xml")
      }
    }).map(_.toURI.toURL).toArray

  addEvents(productFiles, new XmlEventSourceProcessor[ProductUpdate] {
    override def getEventTag(): String = "product"
    override def getEvent(node: Node): ProductUpdate = {
      val ts = (node \ "startDate" text)
      val sku = (node \ "sku" text)
      val name = (node \ "name" text)
      val description = (node \ "longDescription" text)
      val categories = (node \\ "category" \ "name").map(_.text).toArray
      new ProductUpdate(IOUtil.getProductDate(ts), sku.toLong, name, description, categories)
    }
  })

  def save[K](titles: Iterable[String], c: Counter[K])(implicit ord: Ordering[K]): Unit = {
    val t = titles.toArray
    val fn = FileLocations.trainingDir + File.separatorChar + t.map(_.replaceAll(" ","-")).mkString("_") + ".csv"
    val w = new PrintWriter(new FileWriter(fn))
    try {
      w.println(t.mkString(","))
      c.toMap().toArray.sortBy(_._1).map { case (k, v) => k + "," + v }.foreach(w.println)
    } finally {
      w.close()
    }
  }

  def printTiming(): Unit = {
    pm.finished()
  }

}
