package co.ifwe.antelope.datingdemo

object Region extends Enumeration {
  type Region = Value
  val California, Arizona, Nevada, Oregon, Washington, Utah, Idaho = Value
  private def ord(regions: (Region,Region)): (Region,Region) = {
    if (regions._1 < regions._2) {
      regions
    } else {
      (regions._2,regions._1)
    }
  }
  private val bordering = Set() ++
    Array(
      (California,Arizona),(California,Nevada),(California,Oregon),
      (Arizona,Nevada),(Arizona,Utah),
      (Nevada,Utah),(Nevada,Idaho),(Nevada,Oregon),
      (Oregon,Washington),(Oregon,Idaho),
      (Washington,Idaho),
      (Utah,Idaho)
    ).map(ord)

  def borders(a: Region, b: Region) = {
    bordering.contains(ord((a,b)))
  }
}
