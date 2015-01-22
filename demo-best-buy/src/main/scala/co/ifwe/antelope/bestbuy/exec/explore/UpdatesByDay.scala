package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, GregorianCalendar, TimeZone}

import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}

/**
 * This implementation has pretty respectable performance as well as good readability
 */
object UpdatesByDay extends ExploreApp with SimpleState {
  import Utils._

  val updatesByDay = counter { case pu: ProductUpdate => getDate(pu.ts) }

  events.foreach(update(_))

  save(Array("date","updates"), updatesByDay)

  printTiming()
}
