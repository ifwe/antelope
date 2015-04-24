package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

class PrintWatcher (printInterval: Long, printer: (Long, Long) => Unit) {
  var nextPrintTs = 0L
  var lastTs = 0L
  var eventCt = 0L
  def apply(e: Event): Unit = {
    lastTs = e.ts
    eventCt += 1
    if (lastTs >= nextPrintTs) {
      if (nextPrintTs > 0) {
        printer(lastTs, eventCt)
      }
      while (nextPrintTs < lastTs) {
        nextPrintTs += printInterval
      }
    }
  }
  def finish(): Unit = {
    printer(lastTs, eventCt)
  }
}
