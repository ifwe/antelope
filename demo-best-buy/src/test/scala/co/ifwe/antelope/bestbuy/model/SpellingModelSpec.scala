package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.bestbuy.event.ProductUpdate
import org.scalatest.FlatSpec

class SpellingModelSpec extends FlatSpec {
  "A SpellingModel" should "make simple spelling corrections" in {
    val sm = new SpellingModel
    def addTitle(name: String): Unit = {
      sm.update(new ProductUpdate(0L, 0L, name, ""))
    }
    addTitle("Forza Motorsport 4")
    addTitle("Dead Island")
    assert(sm.correct("Dead Island") === None)
    assert(sm.correct("Daed Island") === Some("dead island"))
    assert(sm.correct("Dead Motorsport asdf") === None)
    assert(sm.correct("Ded Motorsport asdf") === Some("dead motorsport asdf"))
  }
}
