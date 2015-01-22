package co.ifwe.antelope

import java.io.{BufferedReader, InputStreamReader}
import java.net.URL

import scala.io.Source

object TestUrl extends App {
//  val oracle = new URL("https://johann.schleier-smith.com/")
  val oracle = this.getClass.getClassLoader.getResource("sample_events.csv")
  val  in = new BufferedReader(new InputStreamReader(oracle.openStream()))
  try {
    for (line <- Source.fromURL(oracle).getLines()) {
      println(line)
    }
  } finally {
    in.close()
  }
}
