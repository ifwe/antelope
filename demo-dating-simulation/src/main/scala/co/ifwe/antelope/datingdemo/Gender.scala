package co.ifwe.antelope.datingdemo

/**
 * In this simulation gender is two-valued and known for all users
 */
object Gender extends Enumeration {
  type Gender = Value
  val Female, Male = Value
}
