package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, TimeZone, GregorianCalendar}

import co.ifwe.antelope.bestbuy.event.ProductView

/**
 * This implementation has pretty respectable performance as well as good readability
 */
object ByHourSimpleState extends ExploreApp with SimpleState {

  val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
  def getHourOfDay(ts: Long): Int = {
    cal.setTimeInMillis(ts)
    cal.get(Calendar.HOUR_OF_DAY)
  }
  def getDayOfWeek(ts: Long): Int = {
    cal.get(Calendar.DAY_OF_WEEK) - 1
  }
  def getDate(ts: Long): String = {
    "%04d%02d%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
  }

  val searchesByHourOfDay = counter { case pv: ProductView => getHourOfDay(pv.ts) }
  val searchesByDayOfWeek = counter { case pv: ProductView => getDayOfWeek(pv.ts) }
  val searchesByDay = counter { case pv: ProductView => getDate(pv.ts) }

  events.foreach(update(_))

  save(Array("hour","searches"), searchesByHourOfDay)
  save(Array("day of week","searches"), searchesByDayOfWeek)
  save(Array("date","searches"), searchesByDay)

  printTiming()
}
