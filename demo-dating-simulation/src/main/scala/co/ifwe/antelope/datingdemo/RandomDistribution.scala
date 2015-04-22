package co.ifwe.antelope.datingdemo

trait RandomDistribution[T] {
  def next(): T
}
