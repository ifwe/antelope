package co.ifwe.antelope.bestbuy.exec.explore.state

trait Counter[K] {
  def get(key: K): Long
  def toMap(): Map[K, Long]
}
