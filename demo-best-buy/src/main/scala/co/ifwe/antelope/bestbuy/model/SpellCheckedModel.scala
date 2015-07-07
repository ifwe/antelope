package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.{Event, Model, ProductSearchScoringContext}

class SpellCheckedModel(m: Model[ProductSearchScoringContext]) extends Model[ProductSearchScoringContext] {
  val sm = new SpellingModel

  override def numFeatures = m.numFeatures

  override def featureNames = m.featureNames

  override def featureValues(ctx: ProductSearchScoringContext, docId: Long): Array[Double] = {
    m.featureValues(ctx, docId)
  }

  /**
   * Update model with new information, provided in event form
   * @param e
   */
  override def update(e: Event): Unit = {
    sm.update(e)
    m.update(e)
  }

  /**
   * Get scores for an array of candidates
   * @param ctx context in which to calculate scores
   * @param candidates identifiers of candidates for which scores are to be computed
   * @param weights model parameters
   * @return array of scores corresponding to array of candidates
   */
  override def score(ctx: ProductSearchScoringContext, candidates: Array[Long], weights: Array[Double]): (Array[Double], ProductSearchScoringContext) = {
    val correctedContext = sm.correct(ctx.query) match {
      case Some(correction) => new ProductSearchScoringContext {
        override val t = ctx.t
        override val query = correction
      }
      case None => ctx
    }
    m.score(correctedContext, candidates, weights)
  }
}
