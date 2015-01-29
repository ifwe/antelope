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

  it should "read a csv with unicode characters" in {
    val url = this.getClass.getClassLoader.getResource("sample_events_utf8.csv")
    val events = CsvEventSourceProcessor[TestEvent](m => new TestEvent(0L, m("state_name"),m("population"))).getEvents(url).toArray

    // Spot check a few elements
    assert(events.head.stateName === ("California"))
    assert(events.head.population === ("38332521"))
    assert(events.contains(new TestEvent(0L, "Michigan","9895622")))
    assert(events.contains(new TestEvent(0L, "把百度设为主页","12882135")))
    assert(events.contains(new TestEvent(0L, "百度不做任何形式的保证","9992167")))
    assert(events.size === 11)
  }
}
