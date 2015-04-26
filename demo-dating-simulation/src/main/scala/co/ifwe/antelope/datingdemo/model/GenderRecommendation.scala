package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.{DatingScoringContext, Gender, User}
import co.ifwe.antelope.datingdemo.event.NewUserEvent

import scala.collection.mutable
import scala.util.Random

class GenderRecommendation(rnd: Random) extends RecommendationSource {
  val maleProfiles = mutable.ArrayBuffer[User]()
  val femaleProfiles = mutable.ArrayBuffer[User]()
  val description = "GenderRecommendation"

  private def randomProfile(profiles: mutable.ArrayBuffer[User]) = {
    profiles(rnd.nextInt(profiles.length))
  }

  override def getRecommendation(ctx: DatingScoringContext): Recommendation = {
    new Recommendation(ctx.user,
      ctx.user.profile.gender match {
      case Gender.Female => randomProfile(maleProfiles)
      case Gender.Male => randomProfile(femaleProfiles)
      }, description)
  }

  override def update(e: Event): Unit = {
    e match {
      case nue: NewUserEvent =>
        val user = nue.user
        (user.profile.gender match {
          case Gender.Female => femaleProfiles
          case Gender.Male => maleProfiles
        }) += user
      case _ =>
    }
  }
}
