package co.ifwe.antelope.io

import java.io.{FileWriter, PrintWriter}

import co.ifwe.antelope.TrainingExample

/**
 * Simultaneous writing of training data in multiple formats
 * @param outputs
 */
class MultiFormatWriter(outputs: Iterable[(String,TrainingFormatter)]) {
  val writers = outputs.map(x => (new PrintWriter(new FileWriter(x._1)), x._2)).toArray
  writers.foreach(x => x._2.header() match {
    case Some(header) => x._1.println(header)
    case None =>
  })

  def write(trainingExample: TrainingExample) = {
    writers.foreach(x => x._1.println(x._2.format(trainingExample)))
  }

  def close(): Unit = {
    writers.foreach(_._1.close)
  }
}
