package co.ifwe.antelope

abstract class EventProcessor {

  protected def init() { }

  protected def consume(e: Event)

  protected def postProcess() { }

  protected def onShutdown() { }

  final def start(): Unit = {
    init()
  }

  final def finish(): Unit = {
    postProcess()
  }

  final def shutdown() {
    onShutdown()
  }

  def process(es: Traversable[Event], limit: Int = 0) {
    if (limit > 0) {
      es.take(limit).foreach(consume)
    } else {
      es.foreach(consume)
    }
  }

}
