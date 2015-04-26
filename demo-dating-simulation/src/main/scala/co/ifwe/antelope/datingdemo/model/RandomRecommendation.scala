package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.{DatingScoringContext, User}
import co.ifwe.antelope.datingdemo.event.NewUserEvent

import scala.collection.mutable
import scala.util.Random

class RandomRecommendation(rnd: Random) extends RecommendationSource {
  val profiles = mutable.ArrayBuffer[User]()
  val description = "RandomRecommendation"

  override def getRecommendation(ctx: DatingScoringContext): Recommendation = {
    new Recommendation(
      forUser = ctx.user,
      profiles(rnd.nextInt(profiles.length)),
      description
    )
  }

  override def update(e: Event): Unit = {
    e match {
      case nue: NewUserEvent => profiles += nue.user
      case _ =>
    }
  }
}
