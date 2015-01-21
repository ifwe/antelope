package co.ifwe.antelope.bestbuy.event

import co.ifwe.antelope.Event

trait EventSource[T <: Event] {
  def getAll: Iterable[T]
}
