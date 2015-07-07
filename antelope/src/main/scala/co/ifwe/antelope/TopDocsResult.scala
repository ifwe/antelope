package co.ifwe.antelope

class TopDocsResult[C <: ScoringContext](val topDocs: Array[Long], val maxResults: Int, val executedCtx: C)
