package co.ifwe.antelope.bestbuy.exec

import java.text.SimpleDateFormat

import co.ifwe.antelope.bestbuy.event.{ProductViewStorage, ProductView}
import co.ifwe.antelope.util.ProgressMeter
import co.ifwe.antelope.{EventSource, Event, EventProcessor}
import co.ifwe.antelope.bestbuy.EventProcessing

object ExploreDataII extends App {
  // Set up event sources
//  val createTime = System.currentTimeMillis()
  var eventCt = 0L

  val viewsFn = "/Users/johann/dev/aml/bestbuy/data_large/train_sorted.csv"
  val viewsFnBin = viewsFn + ".bin"

  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  val backupDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  def getTime(timeStr: String): Long = {
    try {
      df.parse(timeStr).getTime
    } catch {
      case e: java.text.ParseException => backupDf.parse(timeStr).getTime
    }
  }

  def getUser(userStr: String): Long = {
    java.lang.Long.parseUnsignedLong(userStr.substring(0, 16), 16) & 0x7fffffffffffffffL
  }

  val startTime = System.currentTimeMillis()
  println(s"starting tests at ${System.currentTimeMillis() - startTime}")
  val pm = new ProgressMeter(printInterval = 50000)

//  val xx = EventSource.fromFile(viewsFn).view.map(e =>
//    new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong))
//  ProductViewStorage.writeEvents(viewsFnBin, xx)
  val xx = ProductViewStorage.readEvents(viewsFnBin)
  xx .foreach { pv: ProductView => pm.increment() }
  pm.finished()


//  override protected def getEventProcessor(): EventProcessor = new EventProcessor {
//    val pm = new ProgressMeter()
//    override def init(): Unit = {
//    }
//    override protected def consume(e: Event): Unit = {
//      pm.increment()
//    }
//    override def postProcess(): Unit = {
//      pm.finished()
//    }
//  }

}
