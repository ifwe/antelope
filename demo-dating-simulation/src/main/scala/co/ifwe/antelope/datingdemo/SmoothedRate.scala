package co.ifwe.antelope.datingdemo

class SmoothedRate(k: Double) {
  var ct: Double = 0D
  var ts: Long = 0
  def add(newts: Long): Unit = {
    if (newts < ts) {
      ct += math.exp(-k*(ts-newts))
    } else {
      ct = ct * math.exp(-k*(newts-ts)) + 1D
      ts = newts
    }
  }
  def getRate(t: Long): Double = {
    ct * math.exp(-k*(t-ts))
  }
}
