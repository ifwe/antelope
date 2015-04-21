package co.ifwe.antelope.io

import java.net.URL

import scala.io.Source

/**
 * WeightsReader reads a comma-separated list of parameters from a text file.
 * This is used for reading fit parameters saved from R.
 */
object WeightsReader {
  def getWeights(url: URL): Array[Double] = {
    Source.fromURL(url,"UTF-8").getLines().take(1).toList.headOption match {
      case Some(x) => x.split(",").map(_.trim).map { w =>
        if (w.equalsIgnoreCase("NA")) {
          0D
        } else {
          w.toDouble
        }
      }
      case None => Array[Double]()
    }
  }
}
