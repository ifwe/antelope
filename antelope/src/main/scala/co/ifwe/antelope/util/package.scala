package co.ifwe.antelope

import java.text.SimpleDateFormat

/**
 * Basic functionality that makes life a little easier.
 */
package object util {
  /**
   * logit transformation with cutoff to avoid
   * @param x
   * @return log(x / (1 - x)) for x > 1e-5, otherwise log(1e-5 / (1 - 1e-5))
   */
  def logit(x: Double): Double = {
    if (x < 1e-5) {
      logit(1e-5)
    } else {
      math.log(x/(1D-x))
    }
  }

  /**
   * identity
   * @param x
   * @return x
   */
  def ident(x: Double): Double = x

  /**
   * square shortcut
   * @param x
   * @return x*x
   */
  def sq(x: Double): Double = x * x

  def arrayAdd(x: Array[Double], y: Array[Double]): Array[Double] = {
    if (x.length != y.length) {
      throw new IllegalArgumentException("array lengths must mach")
    }
    val res = new Array[Double](x.length)
    for (i <- 0 until x.length) {
      res(i) = x(i) + y(i)
    }
    res
  }

  /**
   * Utility functionality for working with Doubles
   * @param x
   */
  class DoubleExt(x: Double) {
    def div(d: Double): Double = if (d == 0 && x == 0) {
      0D
    } else {
      x / d
    }
  }

  implicit def Double2DoubleExt(x: Double) = new DoubleExt(x)

  private val timestampFormat = new ThreadLocal[SimpleDateFormat] {
    override def initialValue() = {
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    }
  }

  /**
   * Standardized and thread-safe timestamp formatting
   * @param timestamp
   * @return
   */
  def formatTimestamp(timestamp: Long): String = {
    timestampFormat.get().format(timestamp)
  }

  /**
   * Utility for normalizing input text
   * @param s
   */
  class NormalizableString(s: String) {
    def normalize: String = Text.normalize(s)
  }

  implicit def String2NormalizableString(s: String) = new NormalizableString(s)
}
