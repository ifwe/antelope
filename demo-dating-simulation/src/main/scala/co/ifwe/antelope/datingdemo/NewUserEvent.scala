package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.Event

class NewUserEvent(val ts: Long, val user: User) extends Event {
  override def toString = {
    s"$ts> reg ${user.profile} "
  }
}
