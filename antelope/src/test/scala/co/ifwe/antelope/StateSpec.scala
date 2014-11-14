package co.ifwe.antelope

import co.ifwe.antelope.UpdateDefinition._
import org.scalatest.FlatSpec

class StateSpec extends FlatSpec {

  class TestEvent(val id: Long) extends Event {
    override def ts: Long = 0L
  }

  "A State" should "create a simple counter" in {
    val s = new State[ScoringContext]
    val update = defUpdate {
      case e: TestEvent => e.id
    }
    val c = s.counter(update)
    for (i <- 1 to 10) {
      val te = new TestEvent((i*i) % 7)
      c.increment(te)
    }
    assert(c() === 10)
    assert((0 to 6 map(x => c(x))) === Array(1,3,3,0,3,0, 0))
  }

  it should "create a hierarchical counter" in {
    val s = new State[ScoringContext]
    val update1 = defUpdate {
      case e: TestEvent => (e.id * e.id) % 7
    }
    val update2 = defUpdate {
      case e: TestEvent => e.id % 3
    }
    val c = s.counter(update1, update2)
    for (i <- 1 to 10) {
      val te = new TestEvent(i)
      c.increment(te)
    }
    assert(c() === 10)
    assert(c((0L,0L)) ===	0)
    assert(c((1L,1L)) ===	1)
    assert(c((4L,2L)) ===	2)
    assert(c((2L,0L)) ===	1)
    assert(c((2L,1L)) ===	2)
    assert(c((1L,0L)) ===	1)
    assert(c((0L,1L)) ===	1)
    assert(c((1L,2L)) ===	1)
    assert(c((4L,0L)) ===	1)
    assert(c((4L,1L)) ===	0)
    assert((0 to 6 map(x => c(x))) === Array(1,3,3,0,3,0, 0))
  }

  it should "create a simple set" in {
    val st = new State[ScoringContext]
    val update = defUpdate {
      case e: TestEvent => e.id
    }
    val s = st.set(update)
    for (i <- 1 to 10) {
      val te = new TestEvent((i*i) % 7)
      s.add(te)
    }
    assert(s.size() === 4)
    assert((0 to 6 map(x => s.contains(x))) === Array(true, true, true, false, true, false, false))
  }

  it should "create a hierarchical set" in {
    val st = new State[ScoringContext]
    val update1 = defUpdate {
      case e: TestEvent => (e.id * e.id) % 7
    }
    val update2 = defUpdate {
      case e: TestEvent => e.id % 3
    }
    val s = st.set(update1, update2)
    for (i <- 1 to 10) {
      val te = new TestEvent(i)
      s.add(te)
    }
    assert(s.size() === 8)
    assert(s.contains((0L,0L)) ===	false)
    assert(s.contains((1L,1L)) ===	true)
    assert(s.contains((4L,2L)) ===	true)
    assert(s.contains((2L,0L)) ===	true)
    assert(s.contains((2L,1L)) ===	true)
    assert(s.contains((1L,0L)) ===	true)
    assert(s.contains((0L,1L)) ===	true)
    assert(s.contains((1L,2L)) ===	true)
    assert(s.contains((4L,0L)) ===	true)
    assert(s.contains((4L,1L)) ===	false)
    assert((0 to 6 map(x => s.contains(x))) === Array(true, true, true, false, true, false, false))
  }
}
