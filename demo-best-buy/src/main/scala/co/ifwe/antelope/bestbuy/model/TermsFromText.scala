package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.ProductSearchScoringContext
import co.ifwe.antelope.Text.StringToTerms
import co.ifwe.antelope.UpdateDefinition.defVectorUpdate
import co.ifwe.antelope.bestbuy.event.ProductView
import co.ifwe.antelope.feature.Terms


/**
 * Container for encapsulating ability to extract text terms, both from updates as
 * well as from a query context.
 * @param x function that converts search string to terms, e.g., normalizing, tokenizing,
 *          or perhaps creating bigrams
 */
class TermsFromText(x: StringToTerms) extends Terms {

  /**
   * Get terms from a [[co.ifwe.antelope.bestbuy.event.ProductUpdate]]
   */
  override val termsFromUpdate = defVectorUpdate {
    case pv: ProductView => x(pv.query)
  }

  /**
   * Get terms from the query context
   */
  override val termsFromQueryContext = {
    (ctx: ProductSearchScoringContext) =>
      x(ctx.query)
  }
}
