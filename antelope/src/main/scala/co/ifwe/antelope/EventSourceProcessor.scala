package co.ifwe.antelope

import java.net.URL

trait EventSourceProcessor[T<:Event] {
  def getEvents(url: URL): Iterable[T]
}
