package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.Event

case class VoteEvent (val ts: Long,
                      val id: Long,
                      val otherId: Long,
                      val vote: Boolean) extends Event