package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

case class ResponseEvent (ts: Long, id: Long, suggestedId: Long, vote: Boolean) extends Event
