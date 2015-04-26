package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.datingdemo.model.Recommendation

case class ResponseEvent (val ts: Long,
    val id: Long,
    val otherId: Long,
    val vote: Boolean,
    val recommendationInfo: Recommendation) extends VoteEvent
