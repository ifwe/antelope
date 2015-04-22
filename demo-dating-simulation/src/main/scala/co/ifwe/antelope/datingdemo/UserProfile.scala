package co.ifwe.antelope.datingdemo

import co.ifwe.antelope.datingdemo.Gender.Gender
import co.ifwe.antelope.datingdemo.Region.Region

class UserProfile (val id: Long, val gender: Gender, val age: Int, val region: Region) {
  override def toString = {
    s"$id:$age,$gender,$region"
  }
}