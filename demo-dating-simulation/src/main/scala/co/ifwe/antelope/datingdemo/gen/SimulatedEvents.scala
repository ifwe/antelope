package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.Region._
import co.ifwe.antelope.datingdemo.event.NewUserEvent
import co.ifwe.antelope.datingdemo.model.RecommendationSource
import co.ifwe.antelope.datingdemo.{Gender, User, UserProfile}
import co.ifwe.antelope.util.CustomIterator
import org.apache.commons.math3.random.RandomGenerator

import scala.collection.mutable

class SimulatedEvents(rnd: RandomGenerator, rs: RecommendationSource) extends Iterable[Event] {

  class SimulationRunnable(val t: Long,
                            val f: () => Option[Event])
    extends Ordered[SimulationRunnable] {

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

  override def iterator: Iterator[Event] = new CustomIterator[Event] {
    val startTime = 1000000L
    //  val endTime = 1100000L
    val endTime = 2000000L
    val printInterval = 100000L
    var currentTime = startTime

    var q: mutable.PriorityQueue[SimulationRunnable] = _

    override def init(): Unit = {
      q = new mutable.PriorityQueue[SimulationRunnable]()

      implicit val sc = new SimulationContext {
        override def t: Long = currentTime
        override def nextEventTime(k: Double): Long = {
          t + (-Math.log1p(-rnd.nextDouble())/k).toLong
        }
        override def enqueue(t: Long, f: () => Option[Event]): Unit = {
          q += new SimulationRunnable(t, f)
        }
        override def getRecommendationSource: RecommendationSource = rs
      }

      var nextUserId = 1

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
      val selectivityRandom = new BetaRandom(rnd, 1.5, 2.5)
      val regionAffinityRandom = new RegionRandom(rnd)

      class UserGenerator {
        def newUserRate = if (currentTime < 2000000) {
          0.01
        } else {
          0.001
        }
        def nextUser(): User = {
          val p = new UserProfile(
            id = nextUserId,
            gender = Gender(rnd.nextInt(2)),
            age = ageRandom.next(),
            region = randomRegion.next()
          )
          val u = new User(
            profile = p,
            activity = activityRandom.next(),
            selectivity = selectivityRandom.next(),
            regionAffinity = regionAffinityRandom.next(p.region)
          )
          nextUserId += 1
          u
        }
        def registerNext(): Unit = {
          val nextT = sc.nextEventTime(newUserRate)
          queue(nextT)
        }
        def queue(t: Long): Unit = {
          sc.enqueue(t, () => {
            val u = nextUser()
            registerNext()
            Some(new NewUserEvent(t, u))
          })
        }
        // Schedule first user activity
        queue(currentTime)
      }
      new UserGenerator()    
    }
    
    override def advance(): Event = {
      var e: Option[Event] = None
      while (e == None && currentTime <= endTime && !q.isEmpty) {
        val nextExec = q.dequeue()
        currentTime = nextExec.t
        e = nextExec.f()
      }
      e match {
        case Some(e) => e
        case None => null
      }
    }
  }
}
