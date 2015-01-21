package co.ifwe.antelope.bestbuy.exec.explore

import java.util.{Calendar, GregorianCalendar, TimeZone}

import co.ifwe.antelope.{Event, IterableUpdateDefinition, State}
import co.ifwe.antelope.bestbuy.event.ProductView

object ByHourInState extends ExploreApp {
  val s = new State

  val cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"))

  val viewsByTimeOfDay = s.counter(new IterableUpdateDefinition[Int]() {
    override def getFunction: PartialFunction[Event, Iterable[Int]] = {
      case pv: ProductView =>
        cal.setTimeInMillis(pv.ts)
        Array(cal.get(Calendar.HOUR_OF_DAY))
    }
  })

  events.foreach(e => s.update(Array(e)))

  println("searches by time of day")
  viewsByTimeOfDay.toMap.toArray.sortBy(_._1).foreach(println)

  printTiming()
 }
