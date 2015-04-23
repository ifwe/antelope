package co.ifwe.antelope.datingdemo.event

import co.ifwe.antelope.Event
import co.ifwe.antelope.datingdemo.User

class NewUserEvent(val ts: Long, val user: User) extends Event {
  override def toString = {
    s"$ts> reg ${user.profile} "
  }
}
