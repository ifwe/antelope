package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.Event

case class ResponseEvent (ts: Long, id: Long, suggestedId: Long, vote: Boolean) extends Event
