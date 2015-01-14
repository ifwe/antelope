package co.ifwe.antelope.bestbuy

import java.io.{PrintWriter, BufferedWriter, FileWriter}

object IOUtil {

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
