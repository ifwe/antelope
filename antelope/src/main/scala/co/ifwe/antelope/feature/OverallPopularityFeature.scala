package co.ifwe.antelope.feature

import co.ifwe.antelope._

/**
 * Overall popularity is keeps a count since the begnning of input.
 *
 * @param ide
 * @param s
 * @tparam T context type in which we will be scoring this feature
 */
class OverallPopularityFeature[T <: ScoringContext](ide: IdExtractor)(implicit val s: State[T]) extends Feature[T] {
  import co.ifwe.antelope.util._

  val ct = s.counter(ide)

  override def score(implicit ctx: T) = {
    id: Long => ct(id) div ct()
  }
}
