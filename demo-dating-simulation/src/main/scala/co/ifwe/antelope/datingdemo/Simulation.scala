package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.Region._
import org.apache.commons.math3.random.MersenneTwister

import scala.collection.mutable

object Simulation extends App {
  val startTime = 1000000L
//  val endTime = 10000000L
  val endTime = 10000000L
  val printInterval = 100000L
  var t = startTime

  val q = new mutable.PriorityQueue[SimulationRunnable]()

  val rnd = new MersenneTwister(98435435)
  var nextUserId = 1

  def nextEventTime(k: Double) = {
    t + (-Math.log1p(-rnd.nextDouble())/k).toLong
  }
  
  val randomRegion = new MapWeightedRandom(rnd,
    // 2014 census bureau estimates used for populations
    Map(
      California -> 38802500,
      Arizona -> 6731484,
      Nevada -> 2839099,
      Oregon -> 3970239,
      Washington -> 7061530,
      Utah -> 2942902,
      Idaho -> 1634464
    )
  )

  val ageRandom = new RandomDistribution[Int] {
    val r = new BetaRandom(rnd, 2, 6)
    override def next() = {
      18 + (60 * r.next()).toInt
    }
  }

  val activityRandom = new BetaRandom(rnd, 2.5, 1.5)

  class UserGenerator {
    val newUserRate = .01
    def nextUser(): User = {
      val u = new User(
        new UserProfile(
          id = nextUserId,
          gender = Gender(rnd.nextInt(2)),
          age = ageRandom.next(),
          region = randomRegion.next()
        ),
        activity = activityRandom.next()
      )
      nextUserId += 1
      u
    }
    def registerNext(): Unit = {
      val nextT = nextEventTime(newUserRate)
      queue(nextT)
    }
    def queue(t: Long): Unit = {
      Simulation.this.queue(t, () => {
        val u = nextUser()
        registerNext()
        Some(new NewUserEvent(t, u.profile))
      })
    }
    queue(t)
  }

  def queue(t: Long, f: () => Option[Event]): Unit = {
    q += new SimulationRunnable(t, f)
  }

  class SimulationRunnable(
    val t: Long,
    val f: () => Option[Event]
  ) extends Ordered[SimulationRunnable] {
    def compare(that: SimulationRunnable): Int = {
      if (t > that.t) {
        -1
      } else {
        if (t == that.t) {
          0
        } else {
          1
        }
      }
    }
  }

  val profiles = mutable.ArrayBuffer[UserProfile]()

  var queryCt = 0
  def getRecommendation(id: Long): UserProfile = {
    // randomly selected
    queryCt += 1
    profiles(rnd.nextInt(profiles.length))
  }

  def status(): Option[Event] = {
    println(s"$t ${profiles.length} $queryCt ${q.length}")
    queue(t + printInterval, status)
    None
  }
  queue(t + printInterval, status)

  new UserGenerator()
  while (t < endTime && !q.isEmpty) {
    val nextExec = q.dequeue()
    t = nextExec.t
    nextExec.f() match {
      case Some(e) =>
        e match {
          case nue: NewUserEvent => profiles += nue.profile
          case _ =>
        }
//        println(e)
      case None =>
    }
  }

}
