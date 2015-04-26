package co.ifwe.antelope.datingdemo.gen

class AgeAffinity(bias: Double, width: Double) {
  def p(age: Int, otherAge: Int) = {
    math.exp(-math.pow((age+bias-otherAge)/width,4D))
  }
}
