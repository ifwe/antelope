package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.datingdemo.event.{QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.{HasTimeRange, HasRecommendation, PrintWatcher, VoteStats}
import org.apache.commons.math3.random.MersenneTwister

trait SimulationBase extends HasRecommendation with HasTimeRange {
  def doSimulation(): Unit = {
    val genRnd = new MersenneTwister(98435435)

    val recommendationStats = new VoteStats[QueryEvent]()
    val responseStats = new VoteStats[ResponseEvent]()

    val watcher = new PrintWatcher(100000, {
      case (ts: Long, eventCt: Long) =>
        println(
          s"""$ts $eventCt
             |  $recommendationStats
             |  $responseStats""".stripMargin)
        recommendationStats.mark()
        responseStats.mark()
    })

    for (e <- new SimulatedEvents(genRnd, recommendation, startTime, endTime)) {
      e match {
        case qe: QueryEvent => recommendationStats.record(qe)
        case re: ResponseEvent => responseStats.record(re)
        case _ =>
      }
      recommendation.update(e)
      watcher(e)
    }
    watcher.finish()
  }
}
