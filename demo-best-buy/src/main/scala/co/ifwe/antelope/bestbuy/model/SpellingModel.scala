package co.ifwe.antelope.bestbuy.model

import co.ifwe.antelope.Spelling._
import co.ifwe.antelope.Text._
import co.ifwe.antelope.bestbuy.event.ProductUpdate
import co.ifwe.antelope.{Event, IterableUpdateDefinition, State}

class SpellingModel {
  val s = new State

  val titleTerms = s.counter(
    new IterableUpdateDefinition[String] {
      override def getFunction = {
        case e: ProductUpdate => termsExtract(e.name)
      }
    }
  )

  /**
   * Check the input string for spelling errors and suggest corrections
   * if available
   *
   * @param s input string
   * @return Some(corrected string) or None if no correction needed
   */
  def correct(s: String): Option[String] = {
    val rewrites = for (t <- termsExtract(s)) yield {
      if (titleTerms(t) > 0) {
        t -> None
      } else {
        val permutations = removeLetterPermutations(t) ++
          addLetterPermutations(t) ++
          modifyLetterPermutations(t) ++
          transposeLetterPermutations(t)
        t -> (permutations.collect(new PartialFunction[String,(String,Long)] {
          override def isDefinedAt(s: String): Boolean = {
            titleTerms(s) > 0
          }
          override def apply(s: String): (String, Long) = {
            (s, titleTerms(s))
          }
        }).toList.sortBy(_._2).map(_._1).take(1) match {
          case Nil => None
          case List(x) => Some(x)
        })
      }
    }
    if (rewrites.map(_._2).find(_.isDefined).isDefined) {
      Some(rewrites.map {
        case (term, rewrite) =>
          rewrite match {
            case None => term
            case Some(x) => x
        }
      }.mkString(" "))
    } else {
      None
    }
  }

  def update(e: Event): Unit = {
    s.update(Array(e))
  }
}
