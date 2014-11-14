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
