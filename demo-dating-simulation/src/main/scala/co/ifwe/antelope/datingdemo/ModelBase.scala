package co.ifwe.antelope.datingdemo

import java.io.File

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.model.{Recommendation, DatingModel, RandomRecommendation, RecommendationSource}
import co.ifwe.antelope.io.WeightsReader

import scala.util.Random

/**
 * ModelBase contains the dating recommendation engine and related configuration.
 * During the time interval 0 up to trainingEndTime it returns recommendations
 * selected at random. Between trainingEndTime and scoringEndTime the model it
 * returns recommendations selected in accordance with the DatingModel.
 */
trait ModelBase {
  val rnd = new Random(12123)

  val startTime: Long = 1000000
  val trainingEndTime: Long = 2000000
  val scoringEndTime: Long = 2500000


  val trainingDir = System.getenv("ANTELOPE_TRAINING")
  if (trainingDir == null || trainingDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_TRAINING environment variable")
  }

  val trainingFile = trainingDir + File.separator + "demo_dating_training_data.csv"
  val weightsFile = trainingDir + File.separator + "r_logit_coef_dating.txt"

  val weights = WeightsReader.getWeights(new File(weightsFile).toURI.toURL)

  // We define two recommendation sources, the one makes recommendations at random
  // whereas the other makes recommendations according to our dating model
  val randomRec = new RandomRecommendation(rnd)
  val modelRec = new DatingModel(weights)

  val m = new DatingModel(weights)

  val recommendation = new RecommendationSource {
    override def update(e: Event): Unit = {
      ModelBase.this.update(e)
    }

    override def getRecommendation(ctx: DatingScoringContext): Recommendation = {
      (if (ctx.t <= trainingEndTime || rnd.nextDouble() > 0.01) {
        randomRec
      } else {
        modelRec
      }).getRecommendation(ctx)
    }
  }

  def update(e: Event): Unit = {
    randomRec.update(e)
    modelRec.update(e)
  }
}
