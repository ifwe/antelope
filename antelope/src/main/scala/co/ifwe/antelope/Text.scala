package co.ifwe.antelope

/**
 * Normalize input text, extract terms and bigrams
 */
object Text {
  private val cleanRegex = "[\"\\-\\./:,]".r
  private val extraSpacesRegex = "\\s+".r

  type StringToTerms = String => Iterable[String]

  def normalize(s: String): String = extraSpacesRegex.replaceAllIn(cleanRegex.replaceAllIn(s.toLowerCase," ").trim," ")
  def termsExtract(s: String): Iterable[String] = normalize(s).split(" ")
  def bigramsExtract(s: String): Iterable[String] = termsExtract(s).sliding(2).map(_.mkString(" ")).toIterable

  val terms: StringToTerms = (s: String) => termsExtract(s)
  val bigrams: StringToTerms = (s: String) => bigramsExtract(s)
}
