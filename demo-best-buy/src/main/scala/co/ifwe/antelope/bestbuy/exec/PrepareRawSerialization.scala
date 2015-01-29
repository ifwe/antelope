package co.ifwe.antelope.bestbuy.exec

import java.io._

import co.ifwe.antelope.EventSource
import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.bestbuy.exec.explore.Config._
import co.ifwe.antelope.util.ProgressMeter

/**
 * Simple serialization with native types, allows us to test
 * reading in C rather than with Scala.  Could do additional
 * experiments aimed at developing high-performance readers.
 */
object PrepareRawSerialization extends App {
  import co.ifwe.antelope.bestbuy.IOUtil._

  def writeProductViews(fn: String, events: Iterable[ProductView]): Unit = {
    val output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fn)))
    try {
      for (productView <- events) {
        output.writeLong(productView.ts)
        output.writeLong(productView.queryTs)
        output.writeLong(productView.user)
        output.writeLong(productView.skuSelected)
        val queryBytes = productView.query.getBytes("UTF-8")
        if (queryBytes.length > 32767) {
          throw new RuntimeException("query is too long")
        }
        output.writeShort(queryBytes.length)
        output.write(queryBytes)
      }
    } finally {
      output.close()
    }
  }

  val pm = new ProgressMeter()
  val xx = EventSource.fromFile(viewsFn).view.map(e =>
    new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong)).toArray.sortBy(_.ts)
  writeProductViews(viewsFnBinCprog+".small", xx.take(100000))
  writeProductViews(viewsFnBinCprog, xx.toArray.sortBy(_.ts))

  pm.finished()
}
