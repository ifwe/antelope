package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.Spelling._
import co.ifwe.antelope.Text._
import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}

import scala.collection.mutable
import scala.util.Random

/**
 * Analysis of bad predictions and spelling mistakes
 */
class MissAnalysis(spellingPrintCtMax: Int = 0, nonSpellingPrintCtMax: Int = 0) {
  private var missCt = 0
  private var spellingPrintCt = 0
  private var nonSpellingPrintCt = 0

  private val products = mutable.HashMap[Long,ProductUpdate]()
  val s = new State[ProductSearchScoringContext]
  val titleTerms = s.counter(
    new IterableUpdateDefinition[String] {
      override def getFunction = {
        case e: ProductUpdate => termsExtract(e.name)
      }
    }
  )

  private def getTitle(sku: Long): String = {
    products.get(sku) match {
      case Some(pu) => pu.name
      case None => s"MISSING_PRODUCT - SKU: $sku"
    }
  }

  def analyzeSpelling(queryTerms: Iterable[String],
                           titleTerms: Iterable[String],
                           permutations: String => Iterable[String]): List[(String,String)] = {
    val targets = mutable.HashSet[String]() ++ titleTerms
    (for (term <- queryTerms;
         termPermutation <- permutations(term);
         if term != termPermutation && targets.contains(termPermutation)) yield {
        term -> termPermutation
    }).toList
  }

  def spaceSearch(t: Iterable[String], u: Iterable[String]) = {
    val tgt = mutable.HashSet[String]() ++ u
    (if (t.size > 1) {
      t.sliding(2).filter(x => tgt.contains(x.mkString(""))).map(x => x.mkString(" ") -> x.mkString(""))
    } else {
      Nil
    }).toList
  }

  val spellingStats: Array[(String,(Iterable[String], Iterable[String]) => Iterable[(String,String)])] = Array(
    ("extra space", spaceSearch(_,_)),
    ("missingSpace", (x: Iterable[String], y:Iterable[String]) => spaceSearch(y, x)),
    ("extraLetters", analyzeSpelling(_, _, removeLetterPermutations)),
    ("missingLetters", analyzeSpelling(_, _, addLetterPermutations)),
    ("changeLetters", analyzeSpelling(_, _, modifyLetterPermutations)),
    ("transposeLetters", analyzeSpelling(_, _, transposeLetterPermutations))
  )

  val correctionCounts = new Array[Int](spellingStats.size)

  def miss(pv: ProductView, results: Seq[Long]): Unit = {
    missCt += 1
    val queryTerms = Text.termsExtract(pv.query)
    val titleTerms = Text.termsExtract(getTitle(pv.skuSelected))

    val spellingCorrections = spellingStats.map(_._2(queryTerms, titleTerms).toArray)

    if (spellingCorrections.find(_.size > 0).isDefined) {
      correctionCounts(spellingCorrections.indexWhere(_.size > 0)) += 1
      if (spellingPrintCt < spellingPrintCtMax) {
        spellingStats.map(_._1).zip(spellingCorrections).foreach {
          case (desc, corrections) =>
            println(s"""$desc: ${corrections.mkString(",")}""")
        }
        println(
          s"""query: "${pv.query}"
         |selection: ${getTitle(pv.skuSelected)}
         |results: ${results.map(getTitle).mkString("\n         ")}
         """.stripMargin)
        spellingPrintCt += 1
      }
    } else {
      if (nonSpellingPrintCt < nonSpellingPrintCtMax && Random.nextDouble() < 0.1) {
        println(
          s"""Non-spelling miss
         |query: "${pv.query}"
         |selection: ${getTitle(pv.skuSelected)}
         |results: ${results.map(getTitle).mkString("\n         ")}
         """.stripMargin)
        nonSpellingPrintCt += 1
      }
    }
    missCt += 1
  }

  def summarize(): Unit = {
    println("Candidates for spelling correction")
    spellingStats.map(_._1).zip(correctionCounts).foreach {
      case (desc, ct) =>
        println(s"$desc: $ct")
    }
    println(s"total correctable: ${correctionCounts.sum}")
    println(s"total missed $missCt")
  }

  def register(pu: ProductUpdate): Unit = {
    products += pu.sku -> pu
    s.update(List(pu))
  }
}
