package co.ifwe.antelope

/**
 * Base trait for events
 */
trait Event {
  /**
   * Timestamp of this event, in milliseconds of the Unix epoch
   * @return
   */
  def ts: Long
}

object Event {
  /**
   * Companion object provides ordering to sort events chronologically
   */
  implicit val eventOrdering = new Ordering[Event] {
    override def compare(x: Event, y: Event): Int = {
      val tsx = x.ts
      val tsy = y.ts
      if (tsx > tsy) {
        -1
      } else if (tsx == tsy) {
        0
      } else {
        1
      }
    }
  }
}