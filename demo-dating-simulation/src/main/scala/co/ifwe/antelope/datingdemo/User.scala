package co.ifwe.antelope.datingdemo

import java.security.MessageDigest
import java.nio.ByteBuffer

import collection.mutable

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.event.{ResponseEvent, QueryEvent}
import co.ifwe.antelope.datingdemo.exec.Simulation
import co.ifwe.antelope.datingdemo.gen.SimulationContext

class User(val profile: UserProfile,
           private var activity: Double,
           private val selectivity: Double,
           private val regionAffinity: Array[Double])(implicit s: SimulationContext) {

//  println(s"""${profile.region} ${regionAffinity.mkString(",")}""")

  val MAX_RESPONSE_DELAY = 1000000

  val DOUBLE_UNIT = 1.0 / (1L << 53)

  val previousResponses = mutable.HashSet[Long]()

  def combineDouble(id1: Long, id2: Long, extra: Long) = {
    val bytes = new Array[Byte](24)
    val longs = ByteBuffer.wrap(bytes).asLongBuffer()
    longs.put(0, id1)
    longs.put(1, id2)
    longs.put(2, extra)
    val md = MessageDigest.getInstance("SHA-1")
    val res = md.digest(bytes)
    val resInts = ByteBuffer.wrap(res).asIntBuffer()
    val high = (resInts.get(0) & 0x03ffffff).toLong << 27
    val low = (resInts.get(1) & 0x07ffffff).toLong
    (high | low) * DOUBLE_UNIT
  }
//  res4: Long = 4222319432995412684

  def likes(other: User): Boolean = {
    // We limit this simulation to straight profiles to keep things simple
    if (profile.gender != other.profile.gender
      && combineDouble(profile.id, other.profile.id,7930283810011048737L) < selectivity
      && combineDouble(profile.id, other.profile.id,5511114762692428345L) < regionAffinity(other.profile.region.##)) {
      true
    } else {
      false
    }
  }

  private def evaluateLike(user: User): Option[Event] = {
    if (previousResponses.contains(user.profile.id)) {
      None
    } else {
      previousResponses += user.profile.id
      val vote = likes(user)
      Some(new ResponseEvent(s.t, profile.id, user.profile.id, vote))
    }
  }

  private def queueEvaluateLike(user: User): Unit = {
    val evaluateTs = s.nextEventTime(activity * .0001D)
    if (evaluateTs - s.t < MAX_RESPONSE_DELAY) {
      s.enqueue(evaluateTs, () => evaluateLike(user))
    }
    // TODO decrement activity on both
  }

  private def act(): Option[Event] = {
    val recommendation = Simulation.getRecommendation(profile.id)
    val vote = likes(recommendation)
    if (vote) {
      recommendation.queueEvaluateLike(this)
    }
    schedule()
    Some(new QueryEvent(s.t, profile.id, recommendation.profile.id, vote))
  }

  private def schedule() = s.enqueue(s.nextEventTime(activity * .0001D), act)
  schedule()

}
