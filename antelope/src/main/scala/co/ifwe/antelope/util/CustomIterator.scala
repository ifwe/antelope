package co.ifwe.antelope.util

abstract class CustomIterator[T] extends Iterator[T] {

  def init(): Unit = { }
  def advance():T

  init()
  var nextRet: T = advance()

  override def hasNext: Boolean = nextRet != null

  override def next(): T = {
    if (nextRet == null) {
      throw new IllegalStateException()
    }
    val ret = nextRet
    nextRet = advance()
    ret
  }
}