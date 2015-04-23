package co.ifwe.antelope.util

/**
 * CustomIterator provides helper functionality for creating new
 * an Iterators. Implement the init method with setup code.
 * Implement the advance method to return the next item in the
 * sequence, or null if no more remain.
 *
 * @tparam T
 */
abstract class CustomIterator[T] extends Iterator[T] {

  def init(): Unit = {}

  def advance(): T

  init()
  private var nextLoaded = false
  private var nextRet: T = _

  override def hasNext: Boolean = {
    if (!nextLoaded) {
      nextRet = advance()
      nextLoaded = true
    }
    nextRet != null
  }

  override def next(): T = {
    if (!nextLoaded) {
      advance()
    } else {
      nextLoaded = false
      nextRet
    }
  }
}