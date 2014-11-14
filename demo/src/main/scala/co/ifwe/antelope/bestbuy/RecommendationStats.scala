package co.ifwe.antelope.bestbuy

/**
 * Keep track of precision for our recommendations.
 */
class RecommendationStats {
  private var ct = 0
  private var hitCt = 0

  /**
   * We record a hit any time the results contain the targes
   * @param target
   * @param results
   * @return
   */
  def record(target: Long, results: Seq[Long]): Boolean = {
    ct += 1
    val hit = results.contains(target)
    if (hit) { hitCt += 1}
    hit
  }

  /**
   * @return total number of recommendations recorded
   */
  def numRecorded() = ct

  /**
   * @return total number of hits among recommendations recorded
   */
  def numHits() = hitCt

  override def toString = {
    "%d/%d hits for %.1f%%".format(hitCt, ct, 100D * hitCt.toDouble / ct.toDouble)
  }
}
