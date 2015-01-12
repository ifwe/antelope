package co.ifwe.antelope

/**
 * Normalize input text, extract terms and bigrams
 */
object Text {
  private val normalizers: List[String=>String] = List(
    _.toLowerCase(),                            // lower case conversion
    "(?:(?<=\\.|\\s|^)[a-z]\\.)+".r.              // convert acronyms to plain words
      replaceAllIn(_,
        x => x.group(0).replaceAll("\\.","")),
    "[']".r.replaceAllIn(_, ""),                // drop characters and contract words
    "[\"\\-\\./:,]".r.replaceAllIn(_, " "),     // drop characters to whitespace
    (x: String) => x.trim,                      // remove leading or trailing whitespace
    "\\s+".r.replaceAllIn(_," ")                // remove duplicate spaces
  )

  type StringToTerms = String => Iterable[String]

  def normalize(s: String): String = normalizers.foldLeft(s)((x, n) => n(x))

  def termsExtract(s: String): Iterable[String] = normalize(s).split(" ")
  def bigramsExtract(s: String): Iterable[String] = termsExtract(s).sliding(2).map(_.mkString(" ")).toIterable
  def joinedBigramsExtract(s: String): Iterable[String] = termsExtract(s).sliding(2).map(_.mkString("")).toIterable

  val terms: StringToTerms = (s: String) => termsExtract(s)
  val bigrams: StringToTerms = (s: String) => bigramsExtract(s)
}
