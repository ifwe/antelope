package co.ifwe.antelope.bestbuy

import org.scalatest.FlatSpec

class IOUtilSpec extends FlatSpec {

  "An IOUtil" should "parse dates from product files" in {
    val timeStr = "2012-07-02T23:34:13"
    val ts = IOUtil.getProductTime(timeStr)
    assert(ts === 1341272053000L)
  }

}
