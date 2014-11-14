package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.Text._
import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope.util._
import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.event._

import scala.math._

/**
 * All-in one demonstration of creating a model.  In general it is
 * more practical to define the feature classes separately from
 * the model to facilitate reuse, but for demonstrations this is
 * implementation is more straightforward.
 *
 * You can modify [[co.ifwe.antelope.bestbuy.ModelEventProcessor ModelEventProcessor]]
 * and replace reference to [[BestBuyModel]] with this class in
 * order to use it.
 */
class DemoBestBuyModel extends Model[ProductSearchScoringContext] {
  import s._
  type SC = ProductSearchScoringContext

  val skuViewed = defUpdate {
    case x: ProductView => x.skuSelected
  }

  val queryTerms = defVectorUpdate {
    case x: ProductView => normalize(x.query).split(" ")
  }

  val skuUpdated = defUpdate {
    case x: ProductUpdate => x.sku
  }
  val productNameUpdatedTerms = defVectorUpdate {
    case x: ProductUpdate => normalize(x.name).split(" ")
  }

  class OverallPopularity extends Feature[SC] {
    val ct = s.counter(skuViewed)
    override def score(implicit ctx: SC) = {
      id => ct(id) div ct()
    }
  }

  class TermPopularity extends Feature[SC] {
    val ct = s.counter(queryTerms, skuViewed)
    override def score(implicit ctx: SC) = {
      val queryTerms = normalize(ctx.query).split(" ")
      id => queryTerms.map(term => ct(term, id) div ct(term)).product
    }
  }

  class TfIdfFeature extends Feature[SC] {
    val terms = counter(skuUpdated,productNameUpdatedTerms)
    val docsWithTerm = set(productNameUpdatedTerms,skuUpdated)
    val docs = set(skuUpdated)
    override def score(implicit ctx: SC) = {
      val queryTerms = ctx.query.normalize.split(" ")
      val n = docs.size()
      id => (queryTerms map { t =>
        val tf = terms(id, t)
        val df = docsWithTerm.size(t)
        sqrt(tf) * sq(1D + log(n / (1D + df)))
      }).sum
    }
  }

  feature(new OverallPopularity)
  feature(new TermPopularity)
  feature(new TfIdfFeature)
}
