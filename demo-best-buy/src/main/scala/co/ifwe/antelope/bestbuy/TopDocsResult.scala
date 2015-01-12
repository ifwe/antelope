package co.ifwe.antelope.bestbuy

case class TopDocsResult(val originalQuery: String,
  val inferredQuery: Option[String], topDocs: Array[Long],
  maxResults: Int)