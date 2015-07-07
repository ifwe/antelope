package co.ifwe.antelope.model

import scala.collection.mutable.{ArrayBuffer, HashSet}
import scala.util.Random

class AllDocs {
  private val allDocsHash = HashSet[Long]()
  val allDocsArray = ArrayBuffer[Long]()

  /**
   *
   * @param docId
   * @return true if new document, false otherwise
   */
  def addDoc(docId: Long): Boolean = {
    val duplicate = allDocsHash.contains(docId)
    if (!duplicate) {
      allDocsHash += docId
      allDocsArray += docId
    }
    !duplicate
  }

  /**
   *
   * @param rnd
   * @return document selected at random from among all documents seen
   */
  def getRandomDoc(rnd: Random): Long = {
    allDocsArray(rnd.nextInt(allDocsArray.size))
  }
}
