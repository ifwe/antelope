package co.ifwe.antelope

import scala.collection.immutable.Map

trait MapEvent {
  def apply(name: String): String
  def getKeys: Iterable[String]

  override def equals(o: Any) = o match {
    case o: MapEvent => if (o eq this){
      true
    } else {
      o.getKeys.toSet == getKeys.toSet
      var valuesEqual = true
      for (k <- getKeys) {
        if (this(k) != o(k)) {
          valuesEqual = false
        }
      }
      valuesEqual
    }
    case _ => false
  }

  override def toString = {
    "(" + getKeys.map(k => k + ":" + this(k)).mkString(",") + ")"
  }
}

object MapEvent {
  def apply(m: Map[String,String]): MapEvent = {
    new MapEvent {
      def apply(name: String) = m(name)
      def getKeys = m.keys
    }
  }

  def parse(line: String, keys: Map[String,Int]) = {
    new MapEvent {
      val x = line.split(",").map(_.stripPrefix("\"").stripSuffix("\""))
      def apply(name: String) = x(keys(name))
      def getKeys = keys.keys
    }
  }
}