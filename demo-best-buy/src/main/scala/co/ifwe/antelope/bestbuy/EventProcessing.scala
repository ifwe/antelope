package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.EventProcessor

/**
 * Event processing framework for Best Buy data set.  Starts out by processing
 * the entire product catalog, then follows by processing the events.
 *
 * You must set environment variables as follows:
 *
 *   - ANTELOPE_DATA is the directory containing downloaded training data files
 *   - ANTELOPE_TRAINING is the directory for writing training data
 */
trait EventProcessing extends Env {
  protected def runProcessor(ep: EventProcessor): Unit = {
    try {
      ep.start()
      ep.process(eventHistory.getEvents(Long.MinValue, Long.MaxValue, _ => true, _))
      ep.finish()
    } finally {
      ep.shutdown()
    }
  }
}
