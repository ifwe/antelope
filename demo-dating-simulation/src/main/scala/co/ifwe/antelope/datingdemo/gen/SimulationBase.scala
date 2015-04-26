package co.ifwe.antelope.datingdemo.gen

import scala.collection.mutable

import co.ifwe.antelope.datingdemo.event.{QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.model.HasRecommendation
import co.ifwe.antelope.datingdemo.{RecommendationStats, HasTimeRange, PrintWatcher, VoteStats}
import org.apache.commons.math3.random.MersenneTwister

trait SimulationBase extends HasRecommendation with HasTimeRange {
  def doSimulation(): Unit = {
    val genRnd = new MersenneTwister(98435435)

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

    for (e <- new SimulatedEvents(genRnd, recommendation, startTime, endTime)) {
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
