package co.ifwe.antelope.bestbuy

trait TrainingSampling {
  def registerDoc(docId: Long)
  def getDoc(): Long
}
