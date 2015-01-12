package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.Text
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}

import scala.collection.mutable

class MissAnalysis {
  private var missCt = 0
  private val printCtMax = 75

  private val products = mutable.HashMap[Long,ProductUpdate]()

  private def getTitle(sku: Long): String = {
    products.get(sku) match {
      case Some(pu) => pu.name
      case None => s"MISSING_PRODUCT - SKU: $sku"
    }
  }
//
//  private def edits(s: String): Iterable[String] = {
//    s.splitAt()
//  }

  def miss(pv: ProductView, results: Seq[Long]): Unit = {
    val queryTerms = Text.termsExtract(pv.query)
    val titleTerms = Text.termsExtract(getTitle(pv.skuSelected))

//    val qtHash = mutable.HashSet[String]() ++ queryTerms
//    val ttHash = mutable.HashSet[String]() ++ titleTerms

    def spaceSearch(t: Iterable[String], u: Iterable[String]) = {
      val tgt = mutable.HashSet[String]() ++ u
      (if (t.size > 1) {
        t.sliding(2).filter(x => tgt.contains(x.mkString(""))).map(x => x.mkString(" ") -> x.mkString(""))
      } else {
        Nil
      }).toList
    }

    val extraSpace = spaceSearch(queryTerms, titleTerms)
    val missingSpace = spaceSearch(titleTerms, queryTerms)

    if (extraSpace.size > 0 || missingSpace.size > 0) {
      if (missCt < printCtMax) {
        println(extraSpace)
        println(missingSpace)
        println(
          s"""query: "${pv.query}"
         |selection: ${getTitle(pv.skuSelected)}
         |results: ${results.map(getTitle).mkString("\n         ")}
         """.stripMargin)
      }
      missCt += 1
    }
  }

  def summarize(): Unit = {
    println(s"missed with space $missCt")
  }

  def register(pu: ProductUpdate): Unit = {
    products += pu.sku -> pu
  }
}
