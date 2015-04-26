package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope.datingdemo.event.{NewUserEvent, QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.{Gender, DatingScoringContext, User}
import co.ifwe.antelope.{Event, Feature, Model}

import scala.collection.mutable

class DatingModel(weights: Array[Double]) extends Model[DatingScoringContext] with RecommendationSource {
  import s._
  val users = mutable.HashMap[Long,User]()
  val maleProfiles = mutable.ArrayBuffer[Long]()
  val femaleProfiles = mutable.ArrayBuffer[Long]()
  val description = "DatingModel"

  val userId = defUpdate {
    case qe: QueryEvent => qe.ctx.user.profile.id
    case re: ResponseEvent => re.id
    case nue: NewUserEvent => nue.user.profile.id
  }

  val userActivityTime = defUpdate {
    case qe: QueryEvent => qe.ts
    case re: ResponseEvent => re.ts
  }

  // Same region
  feature(new Feature[DatingScoringContext]() {
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      id: Long => if (ctx.user.profile.region == users(id).profile.region) 1 else 0
    }
  })

  // Recent activity
  feature(new Feature[DatingScoringContext]() {
    val lastActivity = map(userId, userActivityTime)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      id: Long => {
        lastActivity.get(id) match {
          case Some(ts) => ctx.t - ts
          case None => 0D
        }
      }
    }
  })

  // Has recent activity
  feature(new Feature[DatingScoringContext]() {
    val lastActivity = map(userId, userActivityTime)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      id: Long => {
        lastActivity.get(id) match {
          case Some(_) => 1D
          case None => 0D
        }
      }
    }
  })

  // Absolute value of age difference
  feature(new Feature[DatingScoringContext]() {
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcAge = ctx.user.profile.age.toDouble
      id: Long => {
        val tgtAge = users(id).profile.age
        math.abs(srcAge - tgtAge)
      }
    }
  })

  // Square of value of age difference
  feature(new Feature[DatingScoringContext]() {
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcAge = ctx.user.profile.age.toDouble
      id: Long => {
        val tgtAge = users(id).profile.age
        (srcAge - tgtAge) * (srcAge - tgtAge)
      }
    }
  })

  /*
   * TODO Add features:
   *  - Generic region-to-region affinity
   *  - Personalized user-to-region affinity
   */

  override def update(e: Event): Unit = {
    e match {
      case nue: NewUserEvent =>
        val user = nue.user
        users += user.profile.id -> user
        (user.profile.gender match {
          case Gender.Female => femaleProfiles
          case Gender.Male => maleProfiles
        }) += user.profile.id
      case _ =>
    }
    s.update(Array(e))
  }

  override def getRecommendation(ctx: DatingScoringContext): Recommendation = {
    val candidates = (ctx.user.profile.gender match {
      case Gender.Female => maleProfiles
      case Gender.Male => femaleProfiles
    }).toArray
    val recommendedUser = users((candidates zip score(ctx, candidates, weights)).sortBy(-_._2).head._1)
    new Recommendation(ctx.user, recommendedUser, description)
  }
}
