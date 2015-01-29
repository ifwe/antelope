package co.ifwe.antelope.bestbuy.exec.explore

import java.io.{BufferedOutputStream, BufferedInputStream, File, FileInputStream}

import co.ifwe.antelope.bestbuy.IOUtil

import scala.io.Source

/**
 * Utility to fit data using R.  This is principally designed to make the
 * workflow easier by allowing us to run everything from SBT.
 */
object RFit extends App {
  val cmd = s"${Config.rCommand} --no-save"
  val p = Runtime.getRuntime.exec(cmd, null, new File(Config.trainingDir))
  val os = new BufferedOutputStream(p.getOutputStream)
  try {
    val rSrc = new BufferedInputStream(new FileInputStream("scripts/r/train.r"))
    try {
      IOUtil.copy(rSrc, os)
    } finally {
      rSrc.close()
    }
  } finally {
    os.close()
  }
  Source.fromInputStream(p.getInputStream).getLines().foreach(println)
  Source.fromInputStream(p.getErrorStream).getLines().foreach(println)
}
