package co.ifwe.antelope

import org.scalatest.{FlatSpec, ShouldMatchers}

class EventSourceSpec extends FlatSpec with ShouldMatchers {
  "A EventSource" should "produce events from a simple file" in {
    val fn = this.getClass.getClassLoader.getResource("sample_events.csv").getPath
    val states = EventSource.fromFile(fn)

    // Spot check a few elements
    states.head("state_name") should be ("California")
    states.head should be (MapEvent(Map("state_name" -> "California", "population" -> "38332521")))
    assert(states.toArray.contains(MapEvent(Map("state_name" -> "Michigan", "population" -> "9895622"))))
  }
}
