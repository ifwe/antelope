package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.{EventHistory, HasEventHistory, Event}
import co.ifwe.antelope.datingdemo._
import co.ifwe.antelope.datingdemo.event.{QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.model.HasRecommendation
import org.apache.commons.math3.random.MersenneTwister

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * The Simulation trait provides the backbone of our simulation
 */
trait Simulation extends HasRecommendation with HasEventHistory {
  // This is a simple pass-through implementation of the event history. It has no memory
  // and so requires all calls to getEvents to be issued before corresponding events are
  // published.
  override val eventHistory = new EventHistory {
    val handlers = new ArrayBuffer[Event => Option[Boolean]]
    var lastEventTimestamp = Long.MinValue
    var maxRegisteredEndTime = Long.MinValue
    override def publishEvent(e: Event): Unit = {
      // TODO check return value and terminate handler if false
      handlers.foreach(_(e))
      lastEventTimestamp = e.ts
    }
    override def getEvents(startTime: Long, endTime: Long, eventFilter: (Event) => Boolean, eventHandler: (Event) => Boolean): Unit = {
      if (startTime < lastEventTimestamp) {
        throw new RuntimeException("start time in past is unsupported")
      }
      if (endTime > maxRegisteredEndTime) {
        maxRegisteredEndTime = endTime
      }
      handlers += ((e: Event) => if(e.ts >= startTime && e.ts < endTime && eventFilter(e)) Some(eventHandler(e)) else None)
    }
  }

  def doSimulation(): Unit = {
    val genRnd = new MersenneTwister(98435435)

    val simulationStartTime = 0L
    val simulationEndTime = eventHistory.maxRegisteredEndTime

    val recommendationStats = new VoteStats[QueryEvent]()
    val responseStats = new VoteStats[ResponseEvent]()
    val statsMap = mutable.HashMap[String,RecommendationStats]()

    def getRecommendationStats(model: String) = {
      statsMap.getOrElseUpdate(model, new RecommendationStats)
    }

    val watcher = new PrintWatcher(100000, {
      case (ts: Long, eventCt: Long) =>
        println(
          s"""$ts $eventCt
             |  $recommendationStats
             |  $responseStats
             |  ${responseStats.rel(recommendationStats)}""".stripMargin)
        recommendationStats.mark()
        responseStats.mark()
        statsMap.foreach {
          case (model,recommendationStats) => println(s"$model $recommendationStats")
        }
    })

    // register the statistics
    eventHistory.getEvents(Long.MinValue, simulationEndTime, _ => true, (e: Event) => {
      watcher(e)
      true
    })

    for (e <- new SimulatedEvents(genRnd, recommendation, simulationStartTime, simulationEndTime)) {
      eventHistory.publishEvent(e)
      e match {
        case qe: QueryEvent =>
          recommendationStats.record(qe)
          getRecommendationStats(qe.recommendationInfo.model).recordRecommendation(qe)
        case re: ResponseEvent =>
          responseStats.record(re)
          getRecommendationStats(re.recommendationInfo.model).recordResponse(re)
        case _ =>
      }
      recommendation.update(e)
      watcher(e)
    }
    watcher.finish()
  }
}
