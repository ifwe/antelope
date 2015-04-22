package co.ifwe.antelope.feature

import co.ifwe.antelope._

/**
 * Recent popularity counters decay exponentially according to the user-supplied
 * time constant.
 *
 * @param ide
 * @param halfLife
 * @param s
 * @tparam T context type in which we will be scoring this feature
 */
class RecentPopularityFeature[T <: TimeScoringContext](ide: IterableUpdateDefinition[(Long,Long)], halfLife: Double)(implicit val s: State[T]) extends Feature[T] {
  import co.ifwe.antelope.util._

  val ct = s.decayingCounter(ide, Math.log(2) / halfLife)

  override def score(implicit ctx: T) = {
    id: Long => ct(ctx.t, id) div ct(ctx.t)
  }

}
