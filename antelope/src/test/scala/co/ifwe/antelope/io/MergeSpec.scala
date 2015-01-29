package co.ifwe.antelope.io

import co.ifwe.antelope.io.Merge.MergeException
import org.scalatest.FlatSpec

class MergeSpec extends FlatSpec {
  "A Merge" should "do an identity merge" in {
    val a = Array(3, 6, 11)
    assert(Merge.merge(List(a.toIterable).toIterable).toArray === Array(3, 6, 11))
  }

  it should "merge two simple lists" in {
    val a = Array(3, 6, 11)
    val b = Array(-1, 7, 15)
    assert(Merge.merge(List(a.toIterable, b.toIterable).toIterable).toArray === Array(-1, 3, 6, 7, 11, 15))
  }

  it should "merge three simple lists" in {
    val a = Array(3, 6, 11)
    val b = Array(-1, 7, 15)
    val c = Array(-100,5,7,100)
    assert(Merge.merge(List(a.toIterable, b.toIterable, c.toIterable).toIterable).toArray
      === Array(-100,-1, 3, 5, 6, 7, 7, 11, 15, 100))
  }

  it should "merge with an empty list" in {
    val a = Array(3, 6, 11)
    val b = List()
    assert(Merge.merge(List(a.toIterable, b.toIterable).toIterable).toArray === Array(3, 6, 11))
  }

  it should "merge two empty lists" in {
    val a = List[Int]()
    val b = List[Int]()
    assert(Merge.merge(List(a.toIterable, b.toIterable).toIterable).toArray === Array())
  }

  it should "throw when out of order input received" in {
    val a = Array(3, 11, 6)
    try {
      Merge.merge(List(a.toIterable).toIterable).toArray
      fail("no exception on out of order input")
    } catch {
      case _: MergeException[_] =>
    }
  }
}
