package co.ifwe.antelope.feature

import co.ifwe.antelope._
import co.ifwe.antelope.util._

/**
 * Basic implementation of Tf-Idf, here as in Lucene.
 *
 * See, e.g., [[https://lucene.apache.org/core/4_3_0/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html TFIDFSimilarity]]
 *
 * @param ide
 * @param t
 * @param s
 * @tparam T
 */
class TfIdfFeature[T <: ProductSearchScoringContext](ide: IdExtractor, t: Terms)(implicit val s: State[T]) extends Feature[T] {
  import s._
  // frequency of terms in the document
  val terms = counter(ide,t.termsFromUpdate)
  val docs = set(ide)
  val docsWithTerm = set(t.termsFromUpdate,ide)
  override def score(implicit ctx: T): (Long) => Double = {
    val queryTerms: Iterable[String] = t.termsFromQueryContext(ctx)
    val n = docs.size()
    id: Long => (queryTerms map { t: String =>
      val tf = terms(id, t)
      val df = docsWithTerm.size(t)
      Math.sqrt(tf) * sq(1D + Math.log(n / (1D + df)))
    }).sum
  }
}
