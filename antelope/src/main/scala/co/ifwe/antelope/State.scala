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
    def toMap: Map[T1, Long]
  }

  trait Counter2[T1,T2] extends Counter {
    def apply(k: (T1,T2)): Long
    def apply(k: T1): Long
    def mapAt(k: T1): Map[T2, Long]
  }

  trait Counter3[T1,T2,T3] extends Counter {
    def apply(k: (T1,T2,T3)): Long
    def apply(k: (T1,T2)): Long
    def apply(k: T1): Long
  }

  trait StringPrefixCounter extends Counter {
    def apply(k: String): Long
    def prefixSearch(prefix: String): Iterable[(String,Long)]
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
    def toSet: scala.collection.Set[(T1,T2)]
    def mapSize(): Map[T1, Long]
  }

  trait Sum {
    def add: PartialFunction[Event, Unit]
    def apply(): Long
  }

  trait Sum0 extends Sum {
    def apply(): Long
  }

  trait Sum1[T1] extends Sum {
    def apply(k: T1): Long
    def toMap: Map[T1, Long]
  }

  trait SmoothedCounter {
    def increment: PartialFunction[Event, Unit]
    def apply(t: Long): Double
  }

  trait SmoothedCounter1[T1] extends SmoothedCounter {
    def apply(t: Long, k: T1): Double
    def toMap(t: Long): Map[T1, Double]
  }

  val counters = mutable.ArrayBuffer[Counter]()
  val sets = mutable.ArrayBuffer[Set]()
  val sums = mutable.ArrayBuffer[Sum]()
  val smoothedCounters = mutable.ArrayBuffer[SmoothedCounter]()
  val features = mutable.ArrayBuffer[Feature[T]]()

  def registerCounter[T <: Counter](c: T): T = {
    counters += c
    c
  }

  def registerSet[T <: Set](s: T): T = {
    sets += s
    s
  }

  def registerSum[T <: Sum](s: T): T = {
    sums += s
    s
  }

  def registerSmoothedCounter[T <: SmoothedCounter](s: T): T = {
    smoothedCounters += s
    s
  }

  def incr[T](m: mutable.HashMap[T,Long], k: T): Unit = {
    m.put(k, m.getOrElse(k, 0L) + 1)
  }

  def incr[T](m: mutable.HashMap[T,Long], k: T, delta: Int): Unit = {
    m.put(k, m.getOrElse(k, 0L) + delta)
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

      override def toMap(): Map[T1, Long] = m.toMap

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

      override def mapAt(k1: T1): Map[T2, Long] = {
        m.filter(_._1._1 == k1).map({ case ((k1,k2),ct) => (k2,ct)}).toMap
      }

      override def toString(): String = {
        m.toString()
      }
    })
  }

  def stringPrefixCounter(d: IterableUpdateDefinition[String]) = {
    registerCounter(new StringPrefixCounter {
      val f = d.getFunction
      val m = new java.util.TreeMap[String, Long]()
      var totalCt = 0L

      override def increment: PartialFunction[Event, Unit] = f andThen (x => x.foreach {
        k => {
          m.put(k, apply(k) + 1)
          totalCt += 1
        }
      })

      override def apply(k: String): Long = {
        val res = m.get(k)
        if (res == null) {
          0L
        } else {
          res
        }
      }

      override def apply(): Long = totalCt

      private def incLastChar(x: String) = {
        x.substring(0, x.length - 1) + (x.charAt(x.length - 1) + 1).toChar
      }

      override def prefixSearch(prefix: String): Iterable[(String, Long)] = {
        import scala.collection.JavaConversions._
        for (e: java.util.Map.Entry[String, Long] <- m.subMap(prefix, incLastChar(prefix)).entrySet()) yield {
          e.getKey -> e.getValue
        }
      }

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

      override def toSet(): scala.collection.Set[(T1, T2)] = s.toSet

      override def mapSize(): Map[T1, Long] = m.toMap

      override def toString(): String = {
        s.toString()
      }
    })
  }

  def smoothedCounter[T1](d: IterableUpdateDefinition[(T1,Long)], smoothing: Double): SmoothedCounter1[T1] = {
    registerSmoothedCounter(new SmoothedCounter1[T1] {
      class ExpCt {
        var lastT: Long = 0L
        var sum: Double = 0D
        def apply(t: Long): Double = {
          // TODO this really shouldn't be happening, has to do with small sorting issues
          if (t > lastT) {
            sum * Math.exp(-smoothing * (t - lastT))
          } else {
            sum
          }
        }
        def inc(t: Long): Unit = {
          val delta = apply(t) + 1
          if (delta.isInfinite && !sum.isInfinite) {
            println("heading towards Inifinity at " + this + " inc " + t)
          }

          if (delta.isNaN && !sum.isNaN) {
            println("heading towards NaN at " + this + " inc " + t)
          }
          sum = apply(t) + 1
          lastT = t
        }
        override def toString(): String = {
          s"$sum:$lastT"
        }
      }
      val f = d.getFunction
      val m = mutable.HashMap[T1, ExpCt]()
      val totalCt = new ExpCt

      override def increment() = f andThen (x => x.foreach {
        case (k,t) => {
          m.getOrElseUpdate(k, new ExpCt()).inc(t)
          totalCt.inc(t)
        }
      })

      override def apply(t: Long): Double = totalCt(t)

      override def apply(t: Long, k: T1): Double = {
//        println(m.get(k))
        m.get(k) match {
          case Some(ec) => ec(t)
          case None => 0D
        }
      }

      override def toMap(t: Long): Map[T1, Double] = {
        m.mapValues(_(t)).toMap
      }

      override def toString(): String = {
        m.toString() + "\n" + totalCt
      }

    })
  }


  def sum(d: IterableUpdateDefinition[Int]): Sum0 = {
    registerSum(new Sum0 {
      val f = d.getFunction
      var sum = 0L
      override def apply(): Long = sum

      override def add: PartialFunction[Event, Unit] = f andThen (x => x.foreach {
        case (delta) => {
          sum += delta
        }
      })
    })
  }

  def sum[T1](d: IterableUpdateDefinition[(T1, Int)]): Sum1[T1] = {
    registerSum(new Sum1[T1] {
      val f = d.getFunction
      val m = mutable.HashMap[T1, Long]()
      var totalCt = 0L

      override def add(): PartialFunction[Event, Unit] = f andThen (x => x.foreach {
        case (k, delta) => {
          incr(m, k, delta)
          totalCt += delta
        }
      })

      override def apply(k: T1): Long = {
        m.getOrElse(k, 0L)
      }

      override def apply(): Long = totalCt

      override def toMap(): Map[T1, Long] = m.toMap

      override def toString(): String = {
        m.toString()
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
    for (e <- events; s <- sums) {
      (s.add orElse defUpdate{ case _ => }.getFunction)(e)
    }
    for (e <- events; s <- smoothedCounters) {
      (s.increment orElse defUpdate{ case _ => }.getFunction)(e)
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
