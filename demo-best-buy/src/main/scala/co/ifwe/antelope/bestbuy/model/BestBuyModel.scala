package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.Text._
import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.event._
import co.ifwe.antelope.feature._

/**
 * A simple model implemented for the
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 *
 * Features are:
 *   - popularity overall
 *   - popularity for terms in query
 *   - popularity for bigrams in query
 *   - Tf-Idf for terms in product name
 *   - Tf-Idf for bigrams in product name
 *
 * This remains a very simple implementation and is readily extended.
 */
class BestBuyModel extends Model[ProductSearchScoringContext] {
  import s._
  type SC = ProductSearchScoringContext

  val skuSelected = defUpdate {
    case pv: ProductView => pv.skuSelected
  }

  val catalogSku = defUpdate {
    case pu: ProductUpdate => pu.sku
  }

//  val spellCheck

//  override def update: Unit = {
//    def update(e: Event): Unit = {
//
//    }
//  }

  feature(new OverallPopularityFeature(skuSelected))
//  for (te <- List(terms, bigrams, spellCheckedTerms)) {
  for (te <- List(terms, bigrams)) {
    feature(new TermPopularityFeature(skuSelected, new TermsFromText(te)))
    feature(new TfIdfFeature(catalogSku, new TermsProductUpdate(te)))
  }

  feature(new TfIdfFeature(catalogSku, new Terms {
    override val termsFromUpdate: IterableUpdateDefinition[String] =  {
      new IterableUpdateDefinition[String] {
        override def getFunction: PartialFunction[Event, Iterable[String]] = {
          case pu: ProductUpdate =>
            Text.joinedBigramsExtract(pu.name)
        }
      }
    }
    override val termsFromQueryContext: (ProductSearchScoringContext) => Iterable[String] = {
      ctx => Text.termsExtract(ctx.query)
    }
  }))

  feature(new TfIdfFeature(catalogSku, new Terms {
    override val termsFromUpdate: IterableUpdateDefinition[String] =  {
      new IterableUpdateDefinition[String] {
        override def getFunction: PartialFunction[Event, Iterable[String]] = {
          case pu: ProductUpdate =>
            Text.termsExtract(pu.name)
        }
      }
    }
    override val termsFromQueryContext: (ProductSearchScoringContext) => Iterable[String] = {
      ctx => Text.joinedBigramsExtract(ctx.query)
    }
  }))

}
