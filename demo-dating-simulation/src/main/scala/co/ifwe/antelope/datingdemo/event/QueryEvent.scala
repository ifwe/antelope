package co.ifwe.antelope.datingdemo.event

class QueryEvent (ts: Long,
                     id: Long,
                     otherId: Long,
                     vote: Boolean)
  extends VoteEvent (ts, id, otherId, vote)
