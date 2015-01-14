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

  private def confirmOrder(t: Traversable[Event]): Traversable[Event] = {
    new Traversable[Event] {
      override def foreach[U](f: (Event) => U): Unit = {
        var lastTs: Long = 0
        t.foreach {
          e: Event =>
            val ts = e.ts
            // Give 1s allowance for out-of-order data
            if (ts < lastTs - 1000) {
              throw new RuntimeException(s"out of sequence data: $lastTs " +
                s"comes before ${ts}, delta ${lastTs - ts} ms")
            }
            lastTs = e.ts
            f(e)
        }
      }
    }
  }

  def process(es: Traversable[Event], limit: Int = 0) {
    if (limit > 0) {
      confirmOrder(es.take(limit)).foreach(consume)
    } else {
      es.foreach(consume)
    }
  }

}
