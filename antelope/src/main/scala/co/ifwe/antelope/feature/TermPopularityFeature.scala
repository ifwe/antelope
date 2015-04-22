package co.ifwe.antelope.feature

import co.ifwe.antelope._
import co.ifwe.antelope.util._

/**
 * Term popularity gives the frequency with which the document is selected
 * when the term is present.
 *
 * @param ide
 * @param t
 * @param s
 * @tparam T context type in which we will be scoring this feature
 */
class TermPopularityFeature[T <: ProductSearchScoringContext](ide: IdExtractor, t: Terms)(implicit val s: State[T])
  extends Feature[ProductSearchScoringContext] {

  val ct = s.counter(t.termsFromUpdate, ide)

  override def score(implicit ctx: ProductSearchScoringContext) = {
    val queryTerms = t.termsFromQueryContext(ctx)
    id: Long => queryTerms.map(term => {
      ct(term, id) div ct(term)
    }).product
  }
}

