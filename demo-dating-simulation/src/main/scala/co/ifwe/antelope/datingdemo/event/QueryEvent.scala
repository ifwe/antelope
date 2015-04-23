package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.Event

case class QueryEvent (ts: Long, id: Long, val suggestedId: Long, vote: Boolean) extends Event
