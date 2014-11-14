package co.ifwe.antelope.io

import co.ifwe.antelope.TrainingExample

/**
 * Provides ability to format training data for use with
 * the powerful distributed machine learning toolkit
 * [[https://github.com/JohnLangford/vowpal_wabbit/ Vowpal Wabbit]].
 *
 * This implementation uses numbers rather than descriptive feature names,
 * which is a file size optimization but perhaps error prone (sensitive
 * to order) and not as practical
 *
 * See also [[VowpalTrainingFormatter]]
 */
class IndexedVowpalTrainingFormatter extends TrainingFormatter {
  // use numbers
  override def format(trainingExample: TrainingExample) = {
    "%s |f %s".format(if (trainingExample.outcome) "+1" else "-1",
      trainingExample.features.map(_._2).zipWithIndex
        .map(x => (x._2+1).toString + ":" + x._1).mkString(" "))
  }
}
