package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.datingdemo.event.VoteEvent

class VoteStats[T <: VoteEvent] {
  var voteCt = 0L
  var yesVoteCt = 0L

  var markedVoteCt = 0L
  var markedYesVoteCt = 0L

  def record(e: T): Unit = {
    voteCt += 1
    if (e.vote) {
      yesVoteCt += 1
    }
  }

  def mark(): Unit = {
    markedVoteCt = voteCt
    markedYesVoteCt = yesVoteCt
  }

  override def toString = {
    def formatHits(ct: Long, yCt: Long) = {
      "%d/%d hits for %.1f%%".format(yCt, ct, 100D * yCt.toDouble / ct.toDouble)
    }
    formatHits(voteCt, yesVoteCt) +
      " | " +
      formatHits(voteCt - markedVoteCt, yesVoteCt - markedYesVoteCt)
  }
}
