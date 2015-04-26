package co.ifwe.antelope.datingdemo.gen

import org.apache.commons.math3.random.RandomGenerator

class AgeAffinityRandom(rnd: RandomGenerator) {
  def next(): AgeAffinity = {
    new AgeAffinity(rnd.nextGaussian() * 10D, math.max(5D,10 * rnd.nextGaussian() + 8D))
  }
}
