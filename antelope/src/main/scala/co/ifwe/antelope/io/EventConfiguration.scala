package co.ifwe.antelope.io

import java.net.URL

import co.ifwe.antelope.{Event, EventSourceProcessor}

import scala.collection.mutable.ArrayBuffer

trait EventConfiguration {
  private val processors = ArrayBuffer[(URL, EventSourceProcessor[_<:Event])]()
  def addEvents[T<:Event](source: URL, ep: EventSourceProcessor[T]): Unit = {
    processors += ((source, ep))
  }
  def addEvents[T<:Event](sources: Iterable[URL], ep: EventSourceProcessor[T]): Unit = {
    processors ++= sources.map(s => (s,ep))
  }
  def events: Iterable[Event] = {
    processors.map{case (url,processor) => processor.getEvents(url)}.flatten.sortBy(_.ts)
  }
}
