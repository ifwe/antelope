package co.ifwe.antelope

import scala.io.Source

trait EventSource extends Iterable[MapEvent] {

}

object EventSource {
  def fromFile(fn: String): EventSource = {
    new EventSource() {
      override def iterator: Iterator[MapEvent] = new Iterator[MapEvent] {
        val lines = Source.fromFile(fn).getLines()
        val header = lines.next()
        val keys = header.split(",").zipWithIndex.toMap
        var nextLine = lines.next()
        override def hasNext: Boolean = nextLine != null

        override def next(): MapEvent = {
          val ret = MapEvent.parse(nextLine, keys)
          nextLine = if (lines.hasNext) {
            lines.next()
          } else {
            null
          }
          ret
        }
      }
    }
  }
}