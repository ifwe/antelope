package co.ifwe.antelope.io

import java.text.SimpleDateFormat
import java.util.TimeZone

import co.ifwe.antelope.Event
import org.scalatest.FlatSpec

class EventConfigurationSpec extends FlatSpec {

  case class TestEvent(ts: Long, stateName: String, population: Int) extends Event { }

  val df = new SimpleDateFormat("yyyy-MM-dd")
  df.setTimeZone(TimeZone.getTimeZone("GMT"))

  "An EventConfiguration" should "import a single resource" in {
    val ec = new EventConfiguration {
      addEvents(this.getClass.getClassLoader.getResource("sample_events.csv"),
        CsvEventSourceProcessor[TestEvent](m =>
          new TestEvent(0L, m("state_name"),m("population").toInt)))
    }
    val events = ec.events.toArray
    assert(events.head.asInstanceOf[TestEvent].stateName === ("California"))
    assert(events.head.asInstanceOf[TestEvent].population === (38332521))
    assert(events.contains(new TestEvent(0L, "Michigan",9895622)))
    assert(events.size === 11)
  }

  it should "import two resources" in {
    val ec = new EventConfiguration {
      addEvents(
        List(
          this.getClass.getClassLoader.getResource("sample_events_dates_1.csv"),
          this.getClass.getClassLoader.getResource("sample_events_dates_2.csv")
        ),
        CsvEventSourceProcessor[TestEvent](m =>
          new TestEvent(df.parse(m("state_created")).getTime, m("state_name"),m("population").toInt)))
    }
    val events = ec.events.toArray
    assert(events.size === 11)
    assert(events.contains(new TestEvent(-4194892800000L, "Michigan",9895622)))
    assert(events.contains(new TestEvent(-3765139200000L, "California",38332521)))
    assert(events.contains(new TestEvent(-4767638400000L, "Illinois",12882135)))
    assert(events.contains(new TestEvent(-5744563200000L, "New Jersey",8899339)))

    // ensure that ordering is preserved
    events.sliding(2).foreach(x => assert(x(1).ts >= x(0).ts, s"out of order at ${x.mkString(",")}"))
  }

}
