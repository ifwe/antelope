package co.ifwe.antelope

trait EventHistory {
  def publishEvent(e: Event)
  def getEvents(startTime: Long, endTime: Long, eventFilter: Event => Boolean, eventHandler: Event => Boolean)
}
