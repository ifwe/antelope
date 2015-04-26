package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.{DatingScoringContext, User}

trait RecommendationSource {
  def getRecommendation(ctx: DatingScoringContext): Recommendation
  def update(e: Event)
}
