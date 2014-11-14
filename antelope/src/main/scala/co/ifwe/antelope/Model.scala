package co.ifwe.antelope

/**
 * Base trait for model definitions
 *
 * @tparam T context type in which we will be scoring this model
 */
trait Model[T <: ScoringContext] {
  implicit val s = new State[T]
  import s._

  def featureNames: Array[String] = ((1 to features.length) map (n => "feature_%d".format(n))).toArray

  def numFeatures: Int = features.length

  /**
   * Point query for a specific candidate document
   * @param ctx
   * @param docId
   * @return
   */
  def featureValues(ctx: T, docId: Long): Array[Double] = {
    s.score(ctx, Array(docId)).head.toArray
  }

  /**
   * Update model with new information, provided in event form
   * @param e
   */
  def update(e: Event): Unit = {
    s.update(Array(e))
  }

  /**
   * Get scores for an array of candidates
   * @param ctx context in which to calculate scores
   * @param candidates identifiers of candidates for which scores are to be computed
   * @param weights model parameters
   * @return array of scores corresponding to array of candidates
   */
  def score(ctx: T, candidates: Array[Long], weights: Array[Double]): Array[Double] = {
    s.score(ctx, candidates).map{y: Iterable[Double] => ((y zip weights) map (x => x._1 * x._2)).sum}.toArray
  }

}
