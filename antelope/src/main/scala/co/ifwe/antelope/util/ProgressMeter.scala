package co.ifwe.antelope.util

/**
 * Simple progress updates written to standard out.
 *
 * @param printInterval interval at which to print progress, in counter updates, default 5000
 */
class ProgressMeter(val printInterval: Long = 5000, val extraInfo: () => String = () => "") {
  val startTime = System.currentTimeMillis()
  var ct: Long = 0L

  def increment(): Unit = {
    ct += 1
    if (ct % printInterval == 0) {
      println("progress %d".format(ct))
    }
  }

  def finished(): Unit = {
    val elapsedTime = System.currentTimeMillis() - startTime
    println("completed %d in %d ms, rate of %d/s".format(ct, elapsedTime, ct*1000/elapsedTime))
    val extraStr = extraInfo()
    if (extraStr != "") {
      println(extraStr)
    }
  }
}
