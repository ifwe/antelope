package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.ProductSearchScoringContext
import co.ifwe.antelope.Text.StringToTerms
import co.ifwe.antelope.UpdateDefinition.defVectorUpdate
import co.ifwe.antelope.bestbuy.event.ProductUpdate
import co.ifwe.antelope.feature.Terms

class TermsProductUpdate(x: StringToTerms) extends Terms {
  override val termsFromUpdate = defVectorUpdate {
    case pu: ProductUpdate => x(pu.name)
  }
  
  override val termsFromQueryContext = {
    (ctx: ProductSearchScoringContext) =>
      x(ctx.query)
  }
}
