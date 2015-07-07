package co.ifwe.antelope.datingdemo.exec

import co.ifwe.antelope._
import co.ifwe.antelope.datingdemo.event.NewUserEvent
import co.ifwe.antelope.datingdemo.gen.Simulation
import co.ifwe.antelope.datingdemo.{DatingEvaluation, DatingScoringContext, ModelBase}
import co.ifwe.antelope.model.{Scoring, Stats}

object Score extends App with Simulation with ModelBase {

  val s = new Scoring[DatingScoringContext,DatingEvaluation] {
    override val eventHistory: EventHistory = Score.eventHistory
    override val model: Model[DatingScoringContext] = Score.m

    // TODO code duplicated with LearnedRankerTraining
    override def newDocUpdated(e: Event): Option[Long] = {
      e match {
        case nue: NewUserEvent => Some(nue.user.profile.id)
        case _ => None
      }
    }

    override def evaluate(e: Event): Option[DatingEvaluation] = {
      // TODO should be tracking statistics here rather than in Simulation
      None
    }
  }
  val st = new Stats[DatingEvaluation]() {
    override def record(evaluation: DatingEvaluation): Unit = { }

    override def update(e: Event): Unit = { }
  }
  s.score(0, trainingEndTime, scoringEndTime, st)
//  st.summarize
  doSimulation()
}
