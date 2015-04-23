package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.Event

trait VoteEvent extends Event {
  val ts: Long
  val id: Long
  val otherId: Long
  val vote: Boolean
}
