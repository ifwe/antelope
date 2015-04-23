package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.datingdemo.DatingScoringContext

class QueryEvent (val ctx: DatingScoringContext, recommendedId: Long, val vote: Boolean)
  extends VoteEvent {
  override val ts = ctx.t
  override val id = ctx.user.profile.id
  override val otherId = recommendedId
}
