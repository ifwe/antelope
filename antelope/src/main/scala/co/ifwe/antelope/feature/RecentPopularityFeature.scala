package co.ifwe.antelope.feature

import co.ifwe.antelope._

class RecentPopularityFeature[T <: TimeScoringContext](ide: IterableUpdateDefinition[(Long,Long)], halfLife: Double)(implicit val s: State[T]) extends Feature[T] {
  import co.ifwe.antelope.util._

  val ct = s.smoothedCounter(ide, Math.log(2) / halfLife)

  override def score(implicit ctx: T) = {
    id: Long => ct(ctx.t, id) div ct(ctx.t)
  }

}
