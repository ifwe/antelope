package co.ifwe.antelope.io

import java.net.URL

import scala.io.Source

object WeightsReader {
  def getWeights(url: URL): Array[Double] = {
    Source.fromURL(url,"UTF-8").getLines().take(1).toList.headOption match {
      case Some(x) => x.split(",").map(_.trim).map { w =>
        if (w.equalsIgnoreCase("NA")) {
          0D
        } else {
          w.toDouble
        }
      }.toArray
      case None => Array[Double]()
    }
  }
}
