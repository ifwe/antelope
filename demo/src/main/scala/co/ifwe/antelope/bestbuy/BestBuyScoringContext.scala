package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.ProductSearchScoringContext

/**
 * Scoring context for the Best Buy challenge - here it's just the search string provided
 * by the user though extensions are possible, e.g., a user id or session id.
 * @param query
 */
case class BestBuyScoringContext(query: String) extends ProductSearchScoringContext
