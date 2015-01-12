package co.ifwe.antelope

object Spelling {

  def removeLetterPermutations(s: String): Iterable[String] = {
    (0 until s.length).map(s.splitAt(_)).map(x=>x._1 + x._2.tail)
  }

  def addLetterPermutations(s: String): Iterable[String] = {
    (0 to s.length).map(s.splitAt(_)).flatMap{ case (h, t) =>
      ('a' to 'z').map(h + _ + t)
    }
  }

  def modifyLetterPermutations(s: String): Iterable[String] = {
    (0 until s.length).flatMap { ind: Int =>
      val c = s.charAt(ind)
      val prefix = s.substring(0, ind)
      val suffix = s.substring(ind + 1, s.length)
      ('a' to 'z').filter(_ != c).map(prefix + _ + suffix)
    }
  }

  def transposeLetterPermutations(s: String): Iterable[String] = {
    (0 until (s.length - 1)).map { ind =>
      val prefix = s.substring(0, ind)
      val suffix = s.substring(ind + 2, s.length)
      prefix + s.charAt(ind + 1) + s.charAt(ind) + suffix
    }
  }

}