package co.ifwe.antelope.bestbuy.event

import java.io._

import co.ifwe.antelope.Event

object Storage {
  def readEvents(fn: String): Iterable[ProductView] = {
    val r = new DataInputStream(new BufferedInputStream(new FileInputStream(fn), 65536))
    // TODO think about how to do exception handling properly here - perhaps return a closeable resource
    new Iterable[ProductView]() {
      override def iterator: Iterator[ProductView] = {
        new Iterator[ProductView] {
          var nextRes: ProductView = read()
          def read(): ProductView = {
            try {
              val ts = r.readLong()
              val queryTs = r.readLong()
              val user = r.readLong()
              val skuSelected = r.readLong()
              val query = r.readUTF()
              new ProductView(ts, queryTs, user, query, skuSelected)
            } catch {
              case e: EOFException => {
                r.close()
                null
              }
            }
          }

          override def hasNext: Boolean = nextRes != null

          override def next(): ProductView = {
            val res = nextRes
            nextRes = read()
            res
          }
        }
      }
    }
  }

  def writeEvents(fn: String, events: Iterable[ProductView]): Unit = {
    val w = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fn)))
    try {
      events.foreach { e =>
        w.writeLong(e.ts)
        w.writeLong(e.queryTs)
        w.writeLong(e.user)
        w.writeLong(e.skuSelected)
        w.writeUTF(e.query)
      }
    } finally {
      w.close()
    }
  }
}
