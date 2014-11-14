package co.ifwe.antelope.feature

import co.ifwe.antelope.{ProductSearchScoringContext, IterableUpdateDefinition}

/**
 * Container for encapsulating ability to extract text terms, both from updates as
 * well as from a query context.  Often we these are related in providing related
 * normalization, tokenization, bigrams, shingles, or other functionality.
 */
trait Terms {
  val termsFromUpdate: IterableUpdateDefinition[String]
  val termsFromQueryContext: ProductSearchScoringContext => Iterable[String]
}
