package co.ifwe.antelope.model

import co.ifwe.antelope.{Evaluation, Event}

trait Stats[T <: Evaluation] {
  def record(evaluation: T)
  def update(e: Event)
}
