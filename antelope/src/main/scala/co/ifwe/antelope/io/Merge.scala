package co.ifwe.antelope.io

import scala.collection.mutable

object Merge {
  private class MergeIterable[T](iterable: Iterable[T]) {
    val it = iterable.iterator
    var nextRet = it.next
    def hasNext = nextRet != null
    def next = {
      val ret = nextRet
      nextRet = if (it.hasNext) {
        it.next
      } else {
        null.asInstanceOf[T]
      }
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
        val pq = new mutable.PriorityQueue[MergeIterable[T]] ++ inputs.map(new MergeIterable(_))
        override def hasNext: Boolean = !pq.isEmpty
        override def next(): T = {
          val m = pq.dequeue()
          val ret = m.next
          if (m.hasNext)
            pq += m
          ret
        }
      }
    }
  }
}
