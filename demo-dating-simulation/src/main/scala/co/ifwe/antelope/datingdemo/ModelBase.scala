package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.model.{DatingModel, RandomRecommendation, RecommendationSource}

import scala.util.Random

trait ModelBase extends HasTimeRange {
  val startTime: Long = 1000000
  val trainingEndTime: Long = 2000000
  val scoringEndTime: Long = 2001000

  val recRand = new Random(12123)
  val randomRec = new RandomRecommendation(recRand)

  val weights = Array[Double](0.95576,-1.155302e-07,-0.02146215)
  val modelRec = new DatingModel(weights)

  def update(e: Event): Unit = {
    randomRec.update(e)
    modelRec.update(e)
  }

  val recommendation = new RecommendationSource {
    override def update(e: Event): Unit = {
      ModelBase.this.update(e)
    }

    override def getRecommendation(ctx: DatingScoringContext): User = {
      (if (ctx.t < trainingEndTime) randomRec else modelRec).getRecommendation(ctx)
    }
  }

}
