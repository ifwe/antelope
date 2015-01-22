package co.ifwe.antelope.util

abstract class CustomIterator[T] extends Iterator[T] {
  var nextRet: T = advance()
  def advance():T
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