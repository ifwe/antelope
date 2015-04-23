package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.datingdemo.model.RecommendationSource

trait HasRecommendation {
  def recommendation: RecommendationSource
}
