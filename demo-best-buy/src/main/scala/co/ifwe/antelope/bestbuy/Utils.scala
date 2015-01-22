package co.ifwe.antelope.bestbuy

import java.util.{Calendar, GregorianCalendar, TimeZone}

object Utils {
  val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))
  def getHourOfDay(ts: Long): Int = {
    cal.setTimeInMillis(ts)
    cal.get(Calendar.HOUR_OF_DAY)
  }
  def getDayOfWeek(ts: Long): Int = {
    cal.setTimeInMillis(ts)
    cal.get(Calendar.DAY_OF_WEEK) - 1
  }
  def getDate(ts: Long): String = {
    cal.setTimeInMillis(ts)
    "%04d%02d%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
  }
}
