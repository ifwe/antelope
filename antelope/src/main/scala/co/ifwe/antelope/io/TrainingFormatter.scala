package co.ifwe.antelope.io

import co.ifwe.antelope.TrainingExample

/**
 * Trait describing ability to write training data
 */
trait TrainingFormatter {
  /**
   * @return header row String, if this format has a header row
   */
  def header(): Option[String] = None

  /**
   * @param trainingExample
   * @return String representing this training example
   */
  def format(trainingExample: TrainingExample): String
}
