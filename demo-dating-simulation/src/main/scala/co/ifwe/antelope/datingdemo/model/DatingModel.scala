package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope.datingdemo.event.{NewUserEvent, QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.{DatingScoringContext, User}
import co.ifwe.antelope.{Event, Feature, Model}

import scala.collection.mutable

class DatingModel extends Model[DatingScoringContext] with RecommendationSource {
  import s._
  val users = mutable.HashMap[Long,User]()

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

  /*
   * Add features:
   *  - Generic region-to-region affinity
   *  - Personalized user-to-region affinity
   */

  override def update(e: Event): Unit = {
    e match {
      case nue: NewUserEvent => users += nue.user.profile.id -> nue.user
      case _ =>
    }
    s.update(Array(e))
  }

  override def getRecommendation(ctx: DatingScoringContext): User = ???
}
