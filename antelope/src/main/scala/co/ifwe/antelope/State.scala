package co.ifwe.antelope

import co.ifwe.antelope.UpdateDefinition._

import scala.collection.mutable

/**
 * Simple in-memory state implementation.
 *
 * This is the functionality to re-implement in order to run on top of scalable
 * stream-processing frameworks.
 *
 * @tparam T context type in which we will be scoring features that build upon this state
 */
class State[T <: ScoringContext] {

  trait Counter {
    def increment: PartialFunction[Event, Unit]
    def apply(): Long
  }

  trait Counter1[T1] extends Counter {
    def apply(k: T1): Long
  }

  trait Counter2[T1,T2] extends Counter {
    def apply(k: (T1,T2)): Long
    def apply(k: T1): Long
  }

  trait Counter3[T1,T2,T3] extends Counter {
    def apply(k: (T1,T2,T3)): Long
    def apply(k: (T1,T2)): Long
    def apply(k: T1): Long
  }

  trait Set {
    def add: PartialFunction[Event, Unit]
    def size(): Long
  }

  trait Set1[T1] extends Set {
    def contains(k: T1): Boolean
  }

  trait Set2[T1,T2] extends Set {
    def size(k: T1): Long
    def contains(k: T1): Boolean
    def contains(k: (T1, T2)): Boolean
  }

  val counters = mutable.ArrayBuffer[Counter]()
  val sets = mutable.ArrayBuffer[Set]()
  val features = mutable.ArrayBuffer[Feature[T]]()

  def registerCounter[T <: Counter](c: T): T = {
    counters += c
    c
  }

  def registerSet[T <: Set](s: T): T = {
    sets += s
    s
  }

  def incr[T](m: mutable.HashMap[T,Long], k: T): Unit = {
    m.put(k, m.getOrElse(k, 0L) + 1)
  }

  def counter[T1](d: IterableUpdateDefinition[T1]): Counter1[T1] = {
    registerCounter(new Counter1[T1] {
      val f = d.getFunction
      val m = mutable.HashMap[T1, Long]()
      var totalCt = 0L

      override def increment() = f andThen (x => x.foreach {
        k => {
          incr(m, k)
          totalCt += 1
        }
      })

      override def apply(k: T1): Long = {
        m.getOrElse(k, 0L)
      }

      override def apply(): Long = totalCt

      override def toString(): String = {
        m.toString()
      }
    })
  }

  def counter[T1,T2](d1: IterableUpdateDefinition[T1], d2: IterableUpdateDefinition[T2]): Counter2[T1,T2] = {
    registerCounter(new Counter2[T1,T2] {
      val f1: PartialFunction[Event, Iterable[T1]] = d1.getFunction
      val f2: PartialFunction[Event, Iterable[T2]] = d2.getFunction
      val m = mutable.HashMap[(T1,T2), Long]()
      val m1 = mutable.HashMap[(T1), Long]()
      var totalCt = 0L

      override def increment() = new PartialFunction[Event, Unit] {
        override def isDefinedAt(e: Event): Boolean = f1.isDefinedAt(e) && f2.isDefinedAt(e)

        override def apply(e: Event): Unit = {
          f1 andThen { f1v =>
            f2 andThen { f2v =>
              for (x1 <- f1v; x2 <- f2v) {
                incr(m,(x1, x2))
                incr(m1,x1)
                totalCt += 1
              }
            } apply(e)
          } apply(e)
        }
      }

      override def apply(k: (T1,T2)): Long = {
        m.getOrElse(k, 0L)
      }

      override def apply(k: T1): Long = {
        m1.getOrElse(k, 0L)
      }

      override def apply(): Long = totalCt

      override def toString(): String = {
        m.toString()
      }
    })
  }

  def set[T1](d: IterableUpdateDefinition[T1]): Set1[T1] = {
    registerSet(new Set1[T1] {
      val f = d.getFunction
      val s = mutable.HashSet[T1]()

      override def add: PartialFunction[Event, Unit] = f andThen {
        x => x.foreach {
          k => s.add(k)
        }
      }

      override def contains(k: T1): Boolean = s.contains(k)

      override def size(): Long = s.size

      override def toString(): String = {
        s.toString()
      }
    })
  }

  def set[T1,T2](d1: IterableUpdateDefinition[T1], d2: IterableUpdateDefinition[T2]): Set2[T1,T2] = {
    registerSet(new Set2[T1,T2] {
      val f1 = d1.getFunction
      val f2 = d2.getFunction
      val s = mutable.HashSet[(T1,T2)]()
      val m = mutable.HashMap[T1, Long]()

      override def size(k: T1): Long = m.getOrElse(k, 0L)

      override def contains(k: T1): Boolean = m.contains(k)

      override def contains(k: (T1, T2)): Boolean = s.contains(k)

      override def size(): Long = s.size

      override def add = new PartialFunction[Event, Unit] {
        override def isDefinedAt(e: Event): Boolean = f1.isDefinedAt(e) && f2.isDefinedAt(e)

        override def apply(e: Event): Unit = {
          f1 andThen { f1v =>
            f2 andThen { f2v =>
              for (x1 <- f1v; x2 <- f2v) {
                val k = (x1,x2)
                if (!s.contains(k)) {
                  incr(m, x1)
                }
                s.add(k)
              }
            } apply(e)
          } apply(e)
        }
      }

      override def toString(): String = {
        s.toString()
      }
    })
  }

  def feature[U <: T](f: Feature[U]): Unit = {
    features += f.asInstanceOf[Feature[T]]
  }

  def update(events: Iterable[Event]): Unit = {
    for (e <- events; c <- counters) {
      (c.increment orElse defUpdate{ case _ => }.getFunction)(e)
    }
    for (e <- events; s <- sets) {
      (s.add orElse defUpdate{ case _ => }.getFunction)(e)
    }
  }

  def score(ctx: T, ids: Array[Long]): Iterable[Iterable[Double]] = {
    val ff = features.map(f => f.score(ctx))
    ids.map(id => ff.map(_(id)))
  }

  /**
   * Debugging functionality
   */
  def printCounters(): Unit = {
    counters.foreach(c => println(c.toString))
  }

  /**
   * Debugging functionality
   */
  def printSets(): Unit = {
    sets.foreach(s => println(s.toString))
  }
}
