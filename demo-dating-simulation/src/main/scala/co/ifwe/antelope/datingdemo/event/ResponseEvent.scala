package co.ifwe.antelope.datingdemo.event

case class ResponseEvent (val ts: Long,
    val id: Long,
    val otherId: Long,
    val vote: Boolean) extends VoteEvent
