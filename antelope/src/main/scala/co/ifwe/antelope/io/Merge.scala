package co.ifwe.antelope.io

import scala.collection.mutable

object Merge {

  class MergeException[T](val x: T, val y: T) extends RuntimeException { }

  private class MergeIterable[T](iterable: Iterable[T]) {
    private val it = iterable.iterator
    var nextRet: T = getNextRet
    private def getNextRet = {
      if (it.hasNext) {
        it.next
      } else {
        null.asInstanceOf[T]
      }
    }
    def hasNext = nextRet != null
    def next: T = {
      val ret = nextRet
      nextRet = getNextRet
      ret
    }
  }

  def merge[T](inputs: Iterable[Iterable[T]])(implicit ord: Ordering[T]): Iterable[T] = {
    implicit val mergeOrdering = new Ordering[MergeIterable[T]] {
      override def compare(x: MergeIterable[T], y: MergeIterable[T]): Int = {
        ord.compare(x.nextRet, y.nextRet)
      }
    }
    new Iterable[T] {
      override def iterator: Iterator[T] = new Iterator[T] {
        val pq = (new mutable.PriorityQueue[MergeIterable[T]] ++ inputs.map(new MergeIterable(_)).filter(_.hasNext)).reverse
        override def hasNext: Boolean = !pq.isEmpty
        var prev: T = null.asInstanceOf[T]
        override def next(): T = {
          val m = pq.dequeue()
          val ret = m.next
          if (prev != null && ord.compare(prev, ret) > 0) {
            throw new MergeException(prev, ret)
          }
          prev = ret
          if (m.hasNext) {
            pq += m
          }
          ret
        }
      }
    }
  }
}
