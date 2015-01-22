package co.ifwe.antelope.util

import java.security.MessageDigest

object Digest {
  def sha1(str: String): Array[Byte] = {
    val md = MessageDigest.getInstance("SHA-1")
    md.digest(str.getBytes("UTF-8"))
  }
}
