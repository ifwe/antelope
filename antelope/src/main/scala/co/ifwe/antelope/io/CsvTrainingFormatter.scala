package co.ifwe.antelope.io

import co.ifwe.antelope.TrainingExample

/**
 * Write training data in CSV format, e.g., for use with R
 * @param columnNames names of columns, in the order they will appear in training examples
 */
class CsvTrainingFormatter(columnNames: Iterable[String]) extends TrainingFormatter {
  override def header(): Option[String] = Some("outcome," + columnNames.mkString(","))

  override def format(trainingExample: TrainingExample) = {
    (if (trainingExample.outcome) "1" else "0") +
      "," + trainingExample.features.map(_._2).mkString(",")
  }
}
