package co.ifwe.antelope.bestbuy.exec

import java.io.File

import co.ifwe.antelope.bestbuy._
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.io.WeightsReader
import co.ifwe.antelope.model.Scoring
import co.ifwe.antelope._

/**
 * Score a some of the documents based on training data - run this
 * program to test or demonstrate effectiveness of the model.
 */
object LearnedRankerScoring extends App with Env {
  val weights = WeightsReader.getWeights(new File(weightsFn).toURI.toURL)
  val st = new BestBuyStats

  val s = new Scoring[ProductSearchScoringContext,BestBuyEvaluation] {
    override val eventHistory: EventHistory = LearnedRankerScoring.eventHistory
    override val model: Model[ProductSearchScoringContext] = LearnedRankerScoring.model
    val ranker = new Ranker(model, weights,allDocs)

    // TODO code duplicated with LearnedRankerTraining
    override def newDocUpdated(e: Event): Option[Long] = {
      e match {
        case pu: ProductUpdate => Some(pu.sku)
        case _ => None
      }
    }

    override def evaluate(e: Event): Option[BestBuyEvaluation] = {
      e match {
        case pv: ProductView =>
          val scoringContext = new ProductSearchScoringContext {
            val t = pv.ts
            val query = pv.query
          }
          val rankedResult = ranker.topN(scoringContext, 5)
          Some(new BestBuyEvaluation(pv, rankedResult.topDocs))
        case _ => None
      }
    }
  }
  s.score(0, TRAINING_LIMIT, SCORING_LIMIT, st)
  st.summarize
}
