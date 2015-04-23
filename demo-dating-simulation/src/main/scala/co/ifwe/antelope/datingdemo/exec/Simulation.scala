package co.ifwe.antelope.datingdemo.exec

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.{VoteStats, User}
import co.ifwe.antelope.datingdemo.event.{NewUserEvent, QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.gen._
import org.apache.commons.math3.random.MersenneTwister

import scala.collection.mutable
import scala.util.Random

object Simulation extends App {
  val profiles = mutable.ArrayBuffer[User]()
  val rnd = new Random(9234809)
  var queryCt = 0
  var queryLikeCt = 0

  def getRecommendation(id: Long): User = {
    // randomly selected
    profiles(rnd.nextInt(profiles.length))
  }

  class PrintWatcher (printInterval: Long, printer: (Long, Long) => Unit) {
    var nextPrintTs = 0L
    var lastTs = 0L
    var eventCt = 0L
    def apply(e: Event): Unit = {
      lastTs = e.ts
      eventCt += 1
      if (lastTs >= nextPrintTs) {
        if (nextPrintTs > 0) {
          printer(lastTs, eventCt)
        }
        nextPrintTs += printInterval
      }
    }
    def finish(): Unit = {
      printer(lastTs, eventCt)
    }
  }

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

  for (e <- new SimulatedEvents(genRnd)) {
    e match {
      case nue: NewUserEvent => profiles += nue.user
      case qe: QueryEvent => recommendationStats.record(qe)
      case re: ResponseEvent => responseStats.record(re)
      case _ =>
    }
    watcher(e)
  }
  watcher.finish()

}
