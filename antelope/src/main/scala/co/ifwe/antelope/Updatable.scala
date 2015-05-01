package co.ifwe.antelope

trait Updatable[T] {
  def update(x: T): Unit
}
