package co.ifwe.antelope.bestbuy

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class UniformTrainingSampling extends TrainingSampling {
  val allDocsSet = new mutable.HashSet[Long]()
  val allDocsArray = new ArrayBuffer[Long]()
  val rnd = new Random(23049L)

  override def registerDoc(docId: Long): Unit = {
    if (!allDocsSet.contains(docId)) {
      allDocsSet += docId
      allDocsArray += docId
    }
  }

  override def getDoc(): Long = {
    allDocsArray(rnd.nextInt(allDocsArray.length))
  }
}
