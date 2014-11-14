package co.ifwe.antelope

import scala.collection.Traversable
import scala.io.Source

trait EventSource extends Traversable[MapEvent] {

}

object EventSource {
  def fromFile(fn: String): EventSource = {
    new EventSource() {
      override def foreach[U](f: (MapEvent) => U) = {
        val lines = Source.fromFile(fn).getLines()
        val header = lines.next()
        val keys = header.split(",").zipWithIndex.toMap
        for (line <- lines) {
          f(MapEvent.parse(line, keys))
        }
      }
    }
  }
}