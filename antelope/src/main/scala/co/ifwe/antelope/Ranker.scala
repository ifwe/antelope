package co.ifwe.antelope

import co.ifwe.antelope.model.AllDocs

class Ranker[C <: ScoringContext](model: Model[C], weights: Array[Double], allDocs: AllDocs) {
  def topN (ctx: C, n: Int): TopDocsResult[C] = {
    val docs = allDocs.allDocsArray.toArray
    val (scores, executedCtx) = model.score (ctx, docs, weights)
    val topDocs = (docs zip  scores).sortBy (- _._2).take(n).map (_._1)
    new TopDocsResult(topDocs, n, executedCtx)
  }
}
