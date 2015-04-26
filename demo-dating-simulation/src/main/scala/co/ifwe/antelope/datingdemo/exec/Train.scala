package co.ifwe.antelope.datingdemo.exec

import java.io.File

import co.ifwe.antelope.datingdemo.event.{ResponseEvent, QueryEvent, VoteEvent}
import co.ifwe.antelope.datingdemo.gen.SimulationBase
import co.ifwe.antelope.{TrainingExample, Event}
import co.ifwe.antelope.datingdemo._
import co.ifwe.antelope.datingdemo.model.{DatingModel, RandomRecommendation, RecommendationSource}
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter}

import scala.util.Random

object Train extends App with SimulationBase with ModelBase {
  val endTime = trainingEndTime

  val trainingDir = System.getenv("ANTELOPE_TRAINING")
  if (trainingDir == null || trainingDir.isEmpty) {
    throw new IllegalArgumentException("must set $ANTELOPE_TRAINING environment variable")
  }

  def getTrainingFile(name: String): String = {
    trainingDir + File.separator + name
  }

  val trainingWriter = new MultiFormatWriter(Array(
    (getTrainingFile("demo_dating_training_data.csv"),
      new CsvTrainingFormatter(modelRec.featureNames))))

  override def update(e: Event): Unit = {
    // Write the training data ahead of updating state
    e match {
      case qe: QueryEvent => {
        if (trainingWriter != null) {
          trainingWriter.write(new TrainingExample(
            qe.vote,
            modelRec.featureNames zip modelRec.featureValues(qe.ctx, qe.otherId)))
        }
      }
      case _ =>
    }
    super.update(e)
  }

  doSimulation()
  trainingWriter.close()
}
