package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.datingdemo.DatingScoringContext
import co.ifwe.antelope.datingdemo.model.Recommendation

class QueryEvent (val ctx: DatingScoringContext, recommendedId: Long, val vote: Boolean,
                   val recommendationInfo: Recommendation)
  extends VoteEvent {
  override val ts = ctx.t
  override val id = ctx.user.profile.id
  override val otherId = recommendedId
}
