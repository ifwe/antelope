package co.ifwe.antelope.bestbuy

import java.io.File
import java.text.SimpleDateFormat

import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.{EventProcessor, EventSource}

/**
 * Event processing framework for Best Buy data set.  Starts out by processing
 * the entire product catalog, then follows by processing the events.
 *
 * You must set environment variables as follows:
 *
 *   - ANTELOPE_DATA is the directory containing downloaded training data files
 *   - ANTELOPE_TRAINING is the directory for writing training data
 */
trait EventProcessing {

  val dataDir = System.getenv("ANTELOPE_DATA")
  if (dataDir == null || dataDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_DATA environment variable")
  }
  val trainingDir = System.getenv("ANTELOPE_TRAINING")
  if (trainingDir == null || trainingDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_TRAINING environment variable")
  }

  val viewsFn = dataDir + File.separator + "train_sorted.csv"
  val productsFn = dataDir + File.separator + "small_product_data.xml"

  protected def getTrainingFile(name: String) = trainingDir + File.separator + name
  protected def productUpdateLimit(): Int = 0
  protected def productViewLimit(): Int = 0
  protected def getEventProcessor(): EventProcessor

  val ep = getEventProcessor()
  try {
    ep.start()

    ep.process(ProductsReader.fromFile(productsFn), productUpdateLimit())

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

    ep.process(EventSource.fromFile(viewsFn).map(e =>
      new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong)),
      productViewLimit())
    ep.finish()
  } finally {
    ep.shutdown()
  }
}
