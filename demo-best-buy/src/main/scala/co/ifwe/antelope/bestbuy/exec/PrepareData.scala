package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.EventSource
import co.ifwe.antelope.bestbuy.event.{Storage, ProductView}
import co.ifwe.antelope.util.ProgressMeter

object PrepareData extends App {
  import co.ifwe.antelope.bestbuy.IOUtil._
  val viewsFn = "/Users/johann/dev/aml/bestbuy/data_large/train_sorted.csv"
  val viewsFnBin = viewsFn + ".bin"

  val pm = new ProgressMeter()
  val xx = EventSource.fromFile(viewsFn).view.map(e =>
    new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong))
  Storage.writeEvents(viewsFnBin, xx)
  pm.finished()


}
