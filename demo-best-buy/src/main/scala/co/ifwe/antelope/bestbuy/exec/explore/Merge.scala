package co.ifwe.antelope.bestbuy.exec.explore

import co.ifwe.antelope.Event
import co.ifwe.antelope.bestbuy.event.Storage

import scala.collection.mutable

object Merge {

  private class MergeFile(fn: String) {
    val it = Storage.readEvents(fn).iterator
    var nextRet = it.next
    def hasNext = nextRet != null
    def next = {
      val ret = nextRet
      nextRet = if (it.hasNext) {
        val newNextRet = it.next
        if (newNextRet.ts < ret.ts) {
          throw new RuntimeException(s"input is not sorted in $fn as ${newNextRet.ts} follows ${ret.ts}")
        }
        newNextRet
      } else {
        null
      }
      ret
    }
  }

  private implicit val mergeOrdering = new Ordering[MergeFile] {
    override def compare(x: MergeFile, y: MergeFile): Int = {
      val tsx = x.nextRet.ts
      val tsy = y.nextRet.ts
      if (tsx > tsy) {
        -1
      } else if (tsx == tsy) {
        0
      } else {
        1
      }
    }
  }

  def merge(inputs: Iterable[String], output: String): Int = {
    var mergeCt = 0
    Storage.writeEvents(output, new Iterable[Event] {
      override def iterator: Iterator[Event] = new Iterator[Event] {
        val pq = new mutable.PriorityQueue[MergeFile] ++ inputs.map(new MergeFile(_))
        override def hasNext: Boolean = !pq.isEmpty
        override def next(): Event = {
          val m = pq.dequeue()
          val ret = m.next
          if (m.hasNext)
            pq += m
          mergeCt += 1
          ret
        }
      }
    })
    mergeCt
  }

}
