package co.ifwe.antelope.io

import co.ifwe.antelope.Event
import org.scalatest.FlatSpec

class CsvEventSourceProcessorSpec extends FlatSpec {
  case class TestEvent(ts: Long, stateName: String, population: String) extends Event { }

  "A CsvEventSourceProcessor" should "read a simple csv" in {
    val url = this.getClass.getClassLoader.getResource("sample_events.csv")
    val events = CsvEventSourceProcessor[TestEvent](m => new TestEvent(0L, m("state_name"),m("population"))).getEvents(url).toArray

    // Spot check a few elements
    assert(events.head.stateName === ("California"))
    assert(events.head.population === ("38332521"))
    assert(events.contains(new TestEvent(0L, "Michigan","9895622")))
    assert(events.size === 11)
  }
}
