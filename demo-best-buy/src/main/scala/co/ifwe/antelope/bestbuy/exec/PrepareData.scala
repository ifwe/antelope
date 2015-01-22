package co.ifwe.antelope.bestbuy.exec

import co.ifwe.antelope.EventSource
import co.ifwe.antelope.bestbuy.event.{Storage, ProductView}
import co.ifwe.antelope.util.ProgressMeter

import co.ifwe.antelope.bestbuy.exec.explore.FileLocations._

object PrepareData extends App {
  import co.ifwe.antelope.bestbuy.IOUtil._

  val pm = new ProgressMeter()
  val xx = EventSource.fromFile(viewsFn).view.map(e =>
    new ProductView(getTime(e("click_time")), getTime(e("query_time")), getUser(e("user")), e("query"), e("sku").toLong))
  Storage.writeEvents(viewsFnBin, xx.toArray.sortBy(_.ts))
  pm.finished()
}
