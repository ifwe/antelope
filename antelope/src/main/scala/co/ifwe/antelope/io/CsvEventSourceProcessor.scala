package co.ifwe.antelope.io

import java.net.URL

import co.ifwe.antelope.{Event, EventSourceProcessor}

import scala.io.Source

abstract class CsvEventSourceProcessor[T<:Event] extends EventSourceProcessor[T] {
  def getEvent(fields: Map[String,String]): T
  override def getEvents(url: URL): Iterable[T] = {
    new Iterable[T] {
      override def iterator = new Iterator[T] {
        val lines = Source.fromURL(url).getLines
        val headerKeys = if (lines.hasNext) lines.next.split(",").zipWithIndex.toMap else null
        var nextRet: T = advance()
        def advance(): T = {
          if (lines.hasNext) {
            getEvent({
              val lineSplit = lines.next.split(",").map(_.stripPrefix("\"").stripSuffix("\""))
              new Map[String,String]() {
                override def get(key: String): Option[String] = {
                  headerKeys.get(key) match {
                    case Some(index) => Some(lineSplit(index))
                    case None => None
                  }
                }
                override def +[B1 >: String](kv: (String, B1)): Map[String, B1] = ???
                override def iterator: Iterator[(String, String)] = ???
                override def -(key: String): Map[String, String] = ???
              }
            })
          } else {
            null.asInstanceOf[T]
          }
        }
        override def hasNext: Boolean = nextRet != null
        override def next(): T = {
          val ret = nextRet
          nextRet = advance()
          ret
        }
      }
    }
  }
}

object CsvEventSourceProcessor {
  def apply[T<:Event](f: Map[String,String] => T) = {
    new CsvEventSourceProcessor[T]() {
      override def getEvent(fields: Map[String, String]): T = f(fields)
    }
  }
}