package co.ifwe.antelope.bestbuy.exec.explore

import java.io.{File, FileWriter, PrintWriter}

import co.ifwe.antelope.bestbuy.IOUtil._
import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.bestbuy.exec.explore.state.Counter
import co.ifwe.antelope.io.{CachedEventConfiguration, CsvEventSourceProcessor, KryoEventStorage}
import co.ifwe.antelope.util.ProgressMeter

trait ExploreApp extends App with CachedEventConfiguration {
  private val startTime = System.currentTimeMillis()
  private val pm = new ProgressMeter(printInterval = 50000)
  println(s"starting tests at ${System.currentTimeMillis() - startTime}")

  override val storage = new KryoEventStorage {
    kryo.register(classOf[ProductView])
  }
  addEvents(new File(FileLocations.viewsFn).toURI.toURL, new CsvEventSourceProcessor[ProductView] {
    override def getEvent(fields: Map[String, String]): ProductView = {
      new ProductView(getTime(fields("click_time")), getTime(fields("query_time")),
        getUser(fields("user")), fields("query"), fields("sku").toLong)
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
