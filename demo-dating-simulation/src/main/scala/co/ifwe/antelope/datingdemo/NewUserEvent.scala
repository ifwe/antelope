package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

class NewUserEvent(val ts: Long, val profile: UserProfile) extends Event {
  override def toString = {
    s"$ts> reg $profile "
  }
}
