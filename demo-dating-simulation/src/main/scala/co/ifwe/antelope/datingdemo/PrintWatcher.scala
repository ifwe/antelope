package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

class PrintWatcher (printInterval: Long, printer: (Long, Long) => Unit) {
  var nextPrintTs = 0L
  var lastTs = 0L
  var eventCt = 0L
  var hasNewEvent = false

  var lastPrintEventCt = 0L
  var lastPrintTs = System.currentTimeMillis()

  def apply(e: Event): Unit = {
    hasNewEvent = true
    lastTs = e.ts
    eventCt += 1
    if (lastTs >= nextPrintTs) {
      if (nextPrintTs > 0) {
        val ts = System.currentTimeMillis()
        println(s"${eventCt - lastPrintEventCt} events at ${(eventCt - lastPrintEventCt) * 1000 / (ts - lastPrintTs)}/s")
        printer(lastTs, eventCt)
        lastPrintEventCt = eventCt
        lastPrintTs = ts
        hasNewEvent = false
      }
      do {
        nextPrintTs += printInterval
      } while (nextPrintTs < lastTs)
    }
  }
  def finish(): Unit = {
    if (hasNewEvent) {
      printer(lastTs, eventCt)
    }
  }
}
