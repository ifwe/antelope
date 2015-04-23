package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.model.RecommendationSource

trait SimulationContext {
  def t: Long
  def nextEventTime(k: Double): Long
  def enqueue(t: Long, f: () => Option[Event]): Unit
  def getRecommendationSource: RecommendationSource
}
