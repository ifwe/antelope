package co.ifwe.antelope.feature

import co.ifwe.antelope._

/**
 * Naive Bayes assumes independence between terms from the query context, an
 * assumption that rarely holds true (certainly not in our examples) but that
 * often proves effective nonetheless. We have the flexibility to combine
 * this as one of several features in a model.
 *
 * @param ide
 * @param t
 * @param s
 * @tparam T context type in which we will be scoring this feature
 */
class NaiveBayesPopularityFeature[T <: ProductSearchScoringContext](ide: IdExtractor, t: Terms)(implicit val s: State[T])
  extends Feature[ProductSearchScoringContext] {

  val ct = s.counter(ide, t.termsFromUpdate)

  override def score(implicit ctx: ProductSearchScoringContext) = {
    val queryTerms = t.termsFromQueryContext(ctx)
    id: Long => queryTerms.map(term => {
      ct(id, term) div ct(id)
    }).product
  }
}

