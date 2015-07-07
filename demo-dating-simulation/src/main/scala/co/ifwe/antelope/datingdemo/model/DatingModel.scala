package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope.datingdemo.event.{NewUserEvent, QueryEvent, ResponseEvent}
import co.ifwe.antelope.datingdemo.{Region, Gender, DatingScoringContext, User}
import co.ifwe.antelope.{Updatable, Event, Feature, Model}

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

  val userRegionUpdate = defUpdate {
    case nue: NewUserEvent => nue.user.profile.region
  }

  val userAgeUpdate = defUpdate {
    case nue: NewUserEvent => nue.user.profile.age
  }

  val userIdVoted = defUpdate {
    case qe: QueryEvent => qe.ctx.user.profile.id
    case re: ResponseEvent => re.id
  }

  val userVote = defUpdate {
    case qe: QueryEvent => qe.vote
    case re: ResponseEvent => re.vote
  }

  // Same region
  feature(new Feature[DatingScoringContext]() {
    val userRegion = map(userId, userRegionUpdate)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcRegion = userRegion(ctx.id)
      id: Long => if (srcRegion == userRegion(id)) 1 else 0
    }
  })

  // Do regions share a border
  feature(new Feature[DatingScoringContext]() {
    val userRegion = map(userId, userRegionUpdate)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcRegion = userRegion(ctx.id)
      id: Long => if (Region.borders(srcRegion, userRegion(id))) 1 else 0
    }
  })

  // Recent activity - how long ago was the user last active
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
    val userAge = map(userId, userAgeUpdate)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcAge: Int = userAge(ctx.id)
      id: Long => {
        val tgtAge = userAge(id)
        math.abs(srcAge - tgtAge)
      }
    }
  })

  // Square of value of age difference
  feature(new Feature[DatingScoringContext]() {
    val userAge = map(userId, userAgeUpdate)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      val srcAge = userAge(ctx.id)
      id: Long => {
        val tgtAge = userAge(id)
        (srcAge - tgtAge) * (srcAge - tgtAge)
      }
    }
  })

  // Overall yes rate and votes above threshold, features used together
  val thresholdVotes = 5
  feature(new Feature[DatingScoringContext]() {
    class VoteRatio extends Updatable[Boolean] {
      var ct = 0
      var yesCt = 0
      override def update(x: Boolean): Unit = {
        ct += 1
        if (x) {
          yesCt += 1
        }
      }
    }
    val voteRatios = mapUpdatable(userId, userVote, new VoteRatio)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      id: Long => {
        voteRatios.get(id) match {
          case Some(vr: VoteRatio) => if (vr.ct >= thresholdVotes) {
              vr.yesCt.toDouble / vr.ct.toDouble
            } else {
              0D
            }
          case None => 0D
        }
      }
    }
  })
  // Voting count above threshold
  feature(new Feature[DatingScoringContext]() {
    val voteCounts = counter(userIdVoted)
    override def score(implicit ctx: DatingScoringContext): (Long) => Double = {
      id: Long => if (voteCounts(id) >= thresholdVotes) 1D else 0D
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
    val recommendedUser = users((candidates zip score(ctx, candidates, weights)._1).sortBy(-_._2).head._1)
    new Recommendation(ctx.user, recommendedUser, description)
  }
}
