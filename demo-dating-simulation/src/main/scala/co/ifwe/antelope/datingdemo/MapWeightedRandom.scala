package co.ifwe.antelope.datingdemo

import org.apache.commons.math3.random.RandomGenerator

import scala.reflect.ClassTag

class MapWeightedRandom[T](rnd: RandomGenerator, weights: Map[T,Int])(implicit tag: ClassTag[T]) {
  val keys: Array[T] = weights.keys.toArray
  val ind = keys.map(weights(_)).scanLeft(0)(_+_).drop(1)
  val max = weights.values.sum
  def next(): T = {
    val tgt = rnd.nextInt(max)
    var i = 0
    while (ind(i) < tgt) { i += 1 }
    keys(i)
  }
}
