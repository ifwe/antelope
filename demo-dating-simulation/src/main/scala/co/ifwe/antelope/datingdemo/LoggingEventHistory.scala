package co.ifwe.antelope.datingdemo

import java.io.{FileOutputStream, BufferedOutputStream}

import co.ifwe.antelope.datingdemo.event._
import co.ifwe.antelope.io.KryoEventStorage
import co.ifwe.antelope.{Event, EventHistory}

import scala.collection.mutable.ArrayBuffer

/**
 * LoggingEventHistory provides a simple single-threaded implementation of the EventHistory
 * interface backed by a file on disk.
 *
 * @param fn file where event history is saved
 */
class LoggingEventHistory(fn: String) extends EventHistory {
  case class Listener(eventFilter: Event => Boolean, eventHandler: Event => Boolean)
  val listeners = ArrayBuffer[Listener]()
  val eventBuffer = ArrayBuffer[Event]()
  val bufferLimit = 1000
  var latestTime = -1L

  val s = new KryoEventStorage {
    kryo.register(classOf[NewUserEvent])
    kryo.register(classOf[QueryEvent])
    kryo.register(classOf[ResponseEvent])
    kryo.register(classOf[VoteEvent])
  }

  val os = new BufferedOutputStream(new FileOutputStream(fn))
  val w = s.getEventWriter(os)
  os.flush()

  override def publishEvent(e: Event): Unit = {
    listeners.filter(_.eventFilter(e)).foreach(_.eventHandler(e))
    // TODO remove expired listeners
    eventBuffer += e
    if (eventBuffer.size >= bufferLimit) {
      eventBuffer.foreach(w.write(_))
      eventBuffer.clear()
    }
  }

  override def getEvents(startTime: Long, endTime: Long, eventFilter: (Event) => Boolean, eventHandler: (Event) => Boolean): Unit = {
    listeners += new Listener(eventFilter, eventHandler)
  }
}
