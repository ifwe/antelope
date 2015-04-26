package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.model.{Recommendation, DatingModel, RandomRecommendation, RecommendationSource}

import scala.util.Random

trait ModelBase extends HasTimeRange {
  val startTime: Long = 1000000
  val trainingEndTime: Long = 2000000
  val scoringEndTime: Long = 2500000

  val recRand = new Random(12123)
  val randomRec = new RandomRecommendation(recRand)

  val weights = Array[Double](0.8738378,-0.01156107,-3.083703e-07,-0.02650975,-0.008755346,-0.002450383)
  val modelRec = new DatingModel(weights)

  def update(e: Event): Unit = {
    randomRec.update(e)
    modelRec.update(e)
  }

  val recommendation = new RecommendationSource {
    override def update(e: Event): Unit = {
      ModelBase.this.update(e)
    }

    override def getRecommendation(ctx: DatingScoringContext): Recommendation = {
      (if (ctx.t <= trainingEndTime || recRand.nextDouble() > 0.01) {
        randomRec
      } else {
        modelRec
      }).getRecommendation(ctx)
    }
  }

}
