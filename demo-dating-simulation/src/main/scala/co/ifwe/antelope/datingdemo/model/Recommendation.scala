package co.ifwe.antelope.datingdemo.model

import co.ifwe.antelope.datingdemo.User

class Recommendation(val forUser: User, val recommendedUser: User, val model: String)
