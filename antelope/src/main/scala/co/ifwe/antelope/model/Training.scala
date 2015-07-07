package co.ifwe.antelope.model

import co.ifwe.antelope._
import co.ifwe.antelope.io.MultiFormatWriter

trait Training[T <: ScoringContext, M <: Model[T]] {
  val eventHistory: EventHistory
  val allDocs: AllDocs = new AllDocs
  def getTrainingExamples(e: Event): Option[Iterable[TrainingExample]]
  def newDocUpdated(e: Event): Option[Long]

  def train(trainingStart: Long, trainingEnd: Long, model: M, trainingWriter: MultiFormatWriter): Unit = {
    eventHistory.getEvents(Long.MinValue, trainingEnd, _ => true, (e: Event) => {
      if (e.ts >= trainingStart) {
        getTrainingExamples(e) match {
          case Some(te) => te.foreach(trainingWriter.write)
          case None =>
        }
      }
      // We don't update for duplicates of a document that already exists
      newDocUpdated(e) match {
        case Some(docId) =>
          if (allDocs.addDoc(docId)) {
            model.update(e)
          }
        case _ =>
          model.update(e)
      }
      true
    })
  }
}
