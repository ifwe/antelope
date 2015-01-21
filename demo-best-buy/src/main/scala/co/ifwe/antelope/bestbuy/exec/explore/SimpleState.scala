package co.ifwe.antelope.bestbuy.exec.explore

import co.ifwe.antelope.Event
import state.Counter

import scala.collection.mutable

trait SimpleState {

  def counter[K](f: PartialFunction[Event, K]): Counter[K] = {
    new Counter[K] {
      val cts = mutable.HashMap[K, Long]()
      register(f andThen { k: K =>
        cts.put(k, cts.getOrElse(k, 0L) + 1)
        Unit
      })
      def get(k: K) = cts.getOrElse(k, 0L)
      def toMap() = cts.toMap
    }
  }

  def update(e: Event): Unit = {
    registered.foreach(_(e))
  }

  private val registered = new mutable.ArrayBuffer[PartialFunction[Event, Unit]]

  private def register[T>:Event](f: PartialFunction[T,Unit]): Unit = {
    registered += f
  }

}
