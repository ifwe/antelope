package co.ifwe.antelope.datingdemo

import org.apache.commons.math3.distribution.GammaDistribution
import org.apache.commons.math3.random.RandomGenerator

class DirichletRandom(rnd: RandomGenerator, val params: Array[Double]) {
  val gammas = params.map(new GammaDistribution(rnd, _, 1))
  def next(): Array[Double] = {
    val x = gammas.map(_.sample())
    val s = x.sum
    x.map(_/s)
  }
}
