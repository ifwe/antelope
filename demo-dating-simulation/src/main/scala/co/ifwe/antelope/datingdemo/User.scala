package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

class User(val profile: UserProfile, private val activity: Double) {
  def act(): Option[Event] = {
    val recommendation = Simulation.getRecommendation(profile.id)
    schedule()
    Some(new QueryEvent(Simulation.t, profile.id, recommendation.id))
  }
  def schedule() = Simulation.queue(Simulation.nextEventTime(activity * .00001D), act)
  schedule()
}
