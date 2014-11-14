package co.ifwe.antelope.feature

import co.ifwe.antelope._
import util._

class TermPopularityFeature[T <: ProductSearchScoringContext](ide: IdExtractor, t: Terms)(implicit val s: State[T])
  extends Feature[ProductSearchScoringContext] {

  val ct = s.counter(ide, t.termsFromUpdate)

  override def score(implicit ctx: ProductSearchScoringContext) = {
    val queryTerms = t.termsFromQueryContext(ctx)
    id: Long => queryTerms.map(term => {
      ct(id, term) div ct()
    }).product
  }
}

