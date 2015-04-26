package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.datingdemo.event.{QueryEvent, ResponseEvent}

class RecommendationStats {
  var ct: Long = 0
  var initialYesCt = 0
  var responseCt = 0
  var responseYesCt = 0

  def recordRecommendation(e: QueryEvent): Unit = {
    ct += 1
    if (e.vote) {
      initialYesCt += 1
    }
  }

  def recordResponse(e: ResponseEvent): Unit = {
    responseCt += 1
    if (e.vote) {
      responseYesCt += 1
    }
  }

  override def toString(): String = {
    def r(x: Long, y: Long) = {
      "%.1f%%".format(100D * y.toDouble / x.toDouble)
    }
    s"$ct|$initialYesCt|$responseCt|$responseYesCt : ${r(ct,initialYesCt)} ${r(initialYesCt,responseCt)} ${r(responseCt,responseYesCt)} : ${r(ct,responseYesCt)}"
  }
}
