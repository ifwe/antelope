package co.ifwe.antelope

import co.ifwe.antelope.UpdateDefinition._
import org.scalatest.FlatSpec

class StateSpec extends FlatSpec {

  class TestEvent(val id: Long, val ts: Long = 0L) extends Event

  class StringTestEvent(val str: String, val ts: Long = 0L) extends Event

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

  it should "create a simple StringPrefixCounter" in {
    val st = new State[ScoringContext]
    val spc = st.stringPrefixCounter(defUpdate {
      case e: StringTestEvent => e.str
    })

    for (i <- 1 to 10) {
      val te = new StringTestEvent(((i*i) % 7).toString)
      spc.increment(te)
    }
    assert(spc() === 10)
    assert((0 to 6 map(x => spc(x.toString))) === Array(1,3,3,0,3,0, 0))
  }

  it should "create a StringPrefixCounter" in {
    val st = new State[ScoringContext]
    val spc = st.stringPrefixCounter(defUpdate {
      case e: StringTestEvent => e.str
    })
    val words = Array("coze","cozen","cozenage","cozener","cozening",
      "cozeningly","cozier","cozily","coziness","cozy",
      "crab","crabbed","crabbedly","crabbedness","crabber",
      "crabbery","crabbing","crabby", "crabcatcher","crabeater",
      "craber")
    var i = 0
    for (word <- words) {
      val te = new StringTestEvent(word)
      for (j <- 0 until ((i % 5) + 1)) {
        spc.increment(te)
      }
      i += 1
    }
    assert(spc() === 61)
    val r1 = spc.prefixSearch("coz")
    assert(r1.size === 10)
    assert(r1.find(_==("cozenage",3)).size === 1)

    assert(spc.prefixSearch("cozef").size === 0)
    assert(spc.prefixSearch("a").size === 0)

    val r2 = spc.prefixSearch("crabb").toArray
    assert(r2.size === 7)
    assert(r2.map(_._2).sum === 20)
  }

  it should "create a simple smoothed counter" in {
    val st = new State[ScoringContext]
    val spc = st.decayingCounter(defUpdate {
      case e: TestEvent => (e.id, e.ts)
    }, Math.log(2D)/1000D)

    for (i <- 1 to 10) {
      val te = new TestEvent(((i*i) % 7), i * 1000 + 5000)
      spc.increment(te)
    }

    def trunc(d: Double) = Math.floor(d * 1e6) / 1e6
    assert(spc(15000) === 1.998046875D)
    assert(trunc(spc(18000)) === 0.249755D)
    assert((0 to 6 map(x => trunc(spc(18000,x)))) === Array(0.015624D, 0.039306D, 0.127929D, 0D, 0.066894D, 0D, 0D))
  }
}
