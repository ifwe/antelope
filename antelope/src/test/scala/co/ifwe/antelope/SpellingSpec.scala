package co.ifwe.antelope

import org.scalatest.FlatSpec
import Spelling._

import scala.collection.mutable.HashSet

class SpellingSpec extends FlatSpec {

  "A Spelling" should "remove letters" in {
    assert(removeLetterPermutations("and").toSet ===
      HashSet("an", "ad", "nd"))
  }

  it should "add letters" in {
    val expected = HashSet[String]()
    expected ++= ('a' to 'z').map(_ + "and")
    expected ++= ('a' to 'z').map("a" + _ + "nd")
    expected ++= ('a' to 'z').map("an" + _ + "d")
    expected ++= ('a' to 'z').map("and" + _)
    assert(addLetterPermutations("and").toSet === expected)
  }

  it should "modify letters" in {
    val expected = HashSet[String]()
    expected ++= ('a' to 'z').map(_ + "nd")
    expected ++= ('a' to 'z').map("a" + _ + "d")
    expected ++= ('a' to 'z').map("an" + _)
    expected -= "and"
    assert(modifyLetterPermutations("and").toSet === expected)
  }

  it should "transpose letters" in {
    val expected = HashSet("natelope", "atnelope", "anetlope", "antleope",
      "anteolpe", "antelpoe", "anteloep")
    assert(transposeLetterPermutations("antelope").toSet === expected)
  }

}
