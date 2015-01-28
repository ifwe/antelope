package co.ifwe.antelope

import org.scalatest.FlatSpec

class EventSpec extends FlatSpec {
  "An Event" should "sort in ascending time order" in {
    case class TestEvent(ts: Long) extends Event
    val events: Array[Event] = Array(TestEvent(5), TestEvent(3), TestEvent(10))
    assert(events.sorted.map(_.ts) === Array(3, 5, 10))
  }
}
