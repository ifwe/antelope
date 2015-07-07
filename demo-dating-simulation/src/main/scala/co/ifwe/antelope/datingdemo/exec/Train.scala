package co.ifwe.antelope.datingdemo.exec

import java.io.File

import co.ifwe.antelope.datingdemo._
import co.ifwe.antelope.datingdemo.event.{NewUserEvent, QueryEvent}
import co.ifwe.antelope.datingdemo.gen.Simulation
import co.ifwe.antelope.io.{CsvTrainingFormatter, MultiFormatWriter}
import co.ifwe.antelope.model.Training
import co.ifwe.antelope.{Event, Model, TrainingExample}

object Train extends App with Simulation with ModelBase {
  val trainingWriter = new MultiFormatWriter(Array(
    (trainingFile, new CsvTrainingFormatter(modelRec.featureNames))))

  val t = new Training[DatingScoringContext, Model[DatingScoringContext]] {
    override val eventHistory = Train.eventHistory
    override def newDocUpdated(e: Event): Option[Long] = {
      e match {
        case nue: NewUserEvent => Some(nue.user.profile.id)
        case _ => None
      }
    }

    override def getTrainingExamples(e: Event): Option[Iterable[co.ifwe.antelope.TrainingExample]] = {
      e match {
        case qe: QueryEvent =>
          Some(Array(new TrainingExample(
            qe.vote,
            modelRec.featureNames zip modelRec.featureValues(qe.ctx, qe.otherId))).toIterable)
        case _ => None
      }
    }
  }
  try {
    t.train(startTime,trainingEndTime, m, trainingWriter)
    doSimulation()
    println(s"training output at $trainingFile")
  } finally {
    trainingWriter.close()
  }
}
