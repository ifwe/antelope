package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.ProductSearchScoringContext
import co.ifwe.antelope.bestbuy.model.SpellingModel

/**
 * Scoring context for the Best Buy challenge - here it's just the search string provided
 * by the user though extensions are possible, e.g., a user id or session id.
 * @param rawQuery
 */
class BestBuyScoringContext(rawQuery: String, spellingModel: SpellingModel) extends ProductSearchScoringContext {
  val (query, correction) = spellingModel.correct(rawQuery) match {
    case Some(correction) => (correction, Some(correction))
    case None => (rawQuery, None)
  }
}
