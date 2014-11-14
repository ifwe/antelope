package co.ifwe.antelope.io

import co.ifwe.antelope.TrainingExample

/**
 * Provides ability to format training data for use with
 * the powerful distributed machine learning toolkit
 * [[https://github.com/JohnLangford/vowpal_wabbit/ Vowpal Wabbit]].
 *
 * See also [[IndexedVowpalTrainingFormatter]]
 */
class VowpalTrainingFormatter extends TrainingFormatter {
  override def format(trainingExample: TrainingExample) = {
    "%s |f %s".format(if (trainingExample.outcome) "+1" else "-1",
      trainingExample.features.map(x => x._1+":"+x._2).mkString(" "))
  }
}
