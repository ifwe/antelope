package co.ifwe.antelope.model

import co.ifwe.antelope._

trait Scoring[T <: ScoringContext, U <: Evaluation] {
  val eventHistory: EventHistory
  val model: Model[T]
  val allDocs = new AllDocs
  def newDocUpdated(e: Event): Option[Long]
  def evaluate(e: Event): Option[U]
  def score(inputStart: Long, scoringStart: Long, scoringEnd: Long, stats: Stats[U]): Unit = {
    eventHistory.getEvents(inputStart, scoringEnd, _ => true, (e: Event) => {
      if (e.ts >= scoringStart) {
        evaluate(e) match {
          case Some(evaluation) => stats.record(evaluation)
          case None =>
        }
      }
      model.update(e)
      stats.update(e)
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
