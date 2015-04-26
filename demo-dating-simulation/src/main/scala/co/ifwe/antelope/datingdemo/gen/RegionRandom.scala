package co.ifwe.antelope.datingdemo.gen

import co.ifwe.antelope.datingdemo.Region
import co.ifwe.antelope.datingdemo.Region._
import org.apache.commons.math3.random.RandomGenerator

class RegionRandom(rnd: RandomGenerator) {
  val nRegions = Region.values.size
  val dirichletInputs = Array.fill[Double](nRegions)(1.25)
  val dr = new DirichletRandom(rnd, dirichletInputs)
  def next(homeRegion: Region): Array[Double] = {
    val s = 0.5 * rnd.nextDouble()
    val r = dr.next().map(p => math.log(p/(1D-p))).map(_ + nRegions * s).map(x => 1D/(1D+math.exp(-x)))
    r(homeRegion.##) = 1D
    r
  }
}
