package co.ifwe.antelope.datingdemo.exec

import java.io.File

import co.ifwe.antelope.datingdemo.event.{QueryEvent, VoteEvent}
import co.ifwe.antelope.{TrainingExample, Event}
import co.ifwe.antelope.datingdemo.{DatingScoringContext, User}
import co.ifwe.antelope.datingdemo.model.{DatingModel, RandomRecommendation, RecommendationSource}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter}

import scala.util.Random

object Training extends App with SimulationBase {
  val recRand = new Random(12123)

  val trainingDir = System.getenv("ANTELOPE_TRAINING")
  if (trainingDir == null || trainingDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_TRAINING environment variable")
  }

  def getTrainingFile(name: String): String = {
    trainingDir + File.separator + name
  }

  val m = new DatingModel

  val trainingWriter = new MultiFormatWriter(Array(
    (getTrainingFile("demo_dating_training_data.csv"),
      new CsvTrainingFormatter(m.featureNames))))

  val recommendation = new RecommendationSource {
    val rr = new RandomRecommendation(recRand)

    override def update(e: Event): Unit = {
      e match {
        case qe: QueryEvent => {
          trainingWriter.write(new TrainingExample(qe.vote, m.featureNames zip m.featureValues(qe.ctx, qe.otherId)))
        }
        case _ =>
      }
      rr.update(e)
      m.update(e)
    }

    override def getRecommendation(ctx: DatingScoringContext): User = {
      rr.getRecommendation(ctx)
    }
  }

  doSimulation()
  trainingWriter.close()
}
