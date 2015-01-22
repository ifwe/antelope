package co.ifwe.antelope.bestbuy

import java.io._
import java.text.SimpleDateFormat
import java.util.TimeZone

object IOUtil {

  private def dateFormat(fmt: String) = {
    val df = new SimpleDateFormat(fmt)
    df.setTimeZone(TimeZone.getTimeZone("GMT"))
    df
  }

  private val df = dateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  private val backupDf = dateFormat("yyyy-MM-dd HH:mm:ss")
  def getTime(timeStr: String): Long = {
    try {
      df.parse(timeStr).getTime
    } catch {
      case e: java.text.ParseException => backupDf.parse(timeStr).getTime
    }
  }

  val productDf = dateFormat("yyyy-MM-dd'T'HH:mm:ss")
  def getProductTime(timeStr: String): Long = {
    productDf.parse(timeStr).getTime
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

  def findFiles(path: String, filter: String => Boolean): Iterable[String] = {
    (new File(path)).listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = {
        filter(name)
      }
    }).map(_.getPath)
  }
}
