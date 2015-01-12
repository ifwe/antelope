package co.ifwe.antelope

import org.scalatest.FlatSpec

class TextSpec extends FlatSpec {
  import co.ifwe.antelope.{Text => T}
  val input = "Kinect Sports - Xbox 360"

  "A Text processor" should "convert to lowercase a simple string" in {
    assert(T.normalize(input) === "kinect sports xbox 360")
  }

  it should "clean input of special characters" in {
    assert(T.normalize("space marine") === "space marine")
    assert(T.normalize("\"space marine\"") === "space marine")
    assert(T.normalize("\"space:marine\"") === "space marine")
    assert(T.normalize("\"space/marine\"") === "space marine")
    assert(T.normalize("space/marine") === "space marine")
    assert(T.normalize("spacemarine:") === "spacemarine")
    assert(T.normalize("FIFA Soccer 12 - Xbox 360") === "fifa soccer 12 xbox 360")
  }

  it should "convert newlines to spaces" in {
    val str = """What will next prove a rose.
                |You, of course, are a rose--
                |""".stripMargin
    val expected = "what will next prove a rose you of course are a rose"
    assert(T.normalize(str) === expected)
  }

  it should "extract individual terms" in {
    assert(T.termsExtract(input) === Array("kinect", "sports", "xbox", "360"))
  }

  it should "extract bigrams" in {
    assert(T.bigramsExtract(input) === Array("kinect sports", "sports xbox", "xbox 360"))
  }

  it should "convert acronyms to single words" in {
    assert(T.normalize("F.E.A.R. 3 - Xbox 360") === "fear 3 xbox 360")
    assert(T.normalize("L.A. Noire: Complete Edition - Xbox 360") ===
      "la noire complete edition xbox 360")
    assert(T.normalize("L.A. Noire has an acronym. And another sentence.") ===
     "la noire has an acronym and another sentence")
  }

  it should "drop apostrophes" in {
    assert(T.normalize("Assassin's Creed: Revelations - Xbox 360") ===
      "assassins creed revelations xbox 360")
    assert(T.normalize("You'll enjoy an award-winning trilogy") ===
      "youll enjoy an award winning trilogy")
  }
}
