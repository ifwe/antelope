package co.ifwe.antelope.bestbuy.exec.explore

import co.ifwe.antelope.bestbuy.Utils
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import Utils._

/**
 * This implementation has pretty respectable performance as well as good readability
 */
object ByHourDayWeekday extends ExploreApp with SimpleState {
  val searchesByHourOfDay = counter { case pv: ProductView => getHourOfDay(pv.ts) }
  val searchesByDayOfWeek = counter { case pv: ProductView => getDayOfWeek(pv.ts) }
  val searchesByDay = counter { case pv: ProductView => getDate(pv.ts) }
  val updatesByDay = counter { case pu: ProductUpdate => getDate(pu.ts) }

  events.foreach(update(_))

  save(Array("hour","searches"), searchesByHourOfDay)
  save(Array("day of week","searches"), searchesByDayOfWeek)
  save(Array("date","searches"), searchesByDay)
  save(Array("date","updates"), updatesByDay)

  printTiming()
}
