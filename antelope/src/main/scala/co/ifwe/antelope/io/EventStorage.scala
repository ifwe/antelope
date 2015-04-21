package co.ifwe.antelope.io

import java.io.{InputStream, OutputStream}

import co.ifwe.antelope.Event

/**
 * Interface for writing events to streams and for reading events
 * from streams.
 */
trait EventStorage {
  trait EventWriter {
    def write(e: Event)
    def close(): Unit
  }
  def readEvents(is: InputStream): Iterable[Event]
  def getEventWriter(os: OutputStream): EventWriter
  def writeEvents(os: OutputStream, events: Iterable[Event]): Unit = {
    val w = getEventWriter(os)
    try {
      events.foreach(w.write)
    } finally {
      w.close()
    }
  }
}
