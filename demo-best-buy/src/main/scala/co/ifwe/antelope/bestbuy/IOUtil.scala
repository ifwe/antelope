package co.ifwe.antelope.bestbuy

import java.io.{PrintWriter, BufferedWriter, FileWriter}
import java.text.SimpleDateFormat

object IOUtil {

  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  val backupDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  def getTime(timeStr: String): Long = {
    try {
      df.parse(timeStr).getTime
    } catch {
      case e: java.text.ParseException => backupDf.parse(timeStr).getTime
    }
  }

  def getUser(userStr: String): Long = {
    java.lang.Long.parseUnsignedLong(userStr.substring(0, 16), 16) & 0x7fffffffffffffffL
  }

  trait Writer {
    def write(x: Iterable[String])
  }

  def csvOutput(fn: String, header: Iterable[String], writerFn: Writer => Unit): Unit = {
    val w = new PrintWriter(new BufferedWriter(new FileWriter(fn)))
    try {
      w.println(header.mkString(","))
      writerFn(new Writer() {
        override def write(x: Iterable[String]): Unit = {
          w.println(x.mkString(","))
        }
      })
    } finally {
      w.close()
    }
  }

}
