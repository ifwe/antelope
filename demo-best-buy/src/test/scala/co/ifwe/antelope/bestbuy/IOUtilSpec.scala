package co.ifwe.antelope.bestbuy

import org.scalatest.FlatSpec

class IOUtilSpec extends FlatSpec {

  "An IOUtil" should "parse dates from product files" in {
    val ts = IOUtil.getProductTime("2012-07-02T23:34:13")
    assert(ts === 1341272053000L)
    val ts2 = IOUtil.getProductDate("2011-07-19")
    assert(ts2 === 1311033600000L)
  }
}
