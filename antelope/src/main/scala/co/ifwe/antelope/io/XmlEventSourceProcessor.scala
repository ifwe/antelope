package co.ifwe.antelope.io

import java.net.URL

import co.ifwe.antelope.{Event, EventSourceProcessor}

import scala.xml.{Node, XML}

abstract class XmlEventSourceProcessor[T<:Event] extends EventSourceProcessor[T] {
  def getEvent(node: Node): T
  def getEventTag(): String
  override def getEvents(url: URL): Iterable[T] = 
    (XML.load(url) \\ getEventTag).map(getEvent(_))

}

object XmlEventSourceProcessor {
  def apply[T<:Event](topTag: String, f: Node => T) = {
    new XmlEventSourceProcessor[T]() {
      override def getEventTag(): String = topTag
      override def getEvent(node: Node): T = f(node)
    }
  }
}