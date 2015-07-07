package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.TimeScoringContext

/**
 * Scoring context for the dating simulation. In addition to the scoring time we
 * take as a parameter the user for whom recommendations are to be generated.
 */
class DatingScoringContext(val t: Long, val user: User) extends TimeScoringContext {
  def id: Long = user.profile.id
}