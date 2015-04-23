package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.Event

trait SimulationContext {
  def t: Long
  def nextEventTime(k: Double): Long
  def enqueue(t: Long, f: () => Option[Event]): Unit
}
