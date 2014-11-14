package co.ifwe.antelope

trait IterableUpdateDefinition[T] {
  def getFunction: PartialFunction[Event, Iterable[T]]
  def *[U](x: SimpleUpdateDefinition[U]) = {
    val f1 = IterableUpdateDefinition.this.getFunction
    val f2 = x.getFunction
    new IterableUpdateDefinition[(T,U)] {
      override def getFunction = new PartialFunction[Event, Iterable[(T, U)]] {
        override def isDefinedAt(e: Event): Boolean = f1.isDefinedAt(e) && f2.isDefinedAt(e)

        override def apply(e: Event): Iterable[(T, U)] = {
          val v2 = f2(e)
          f1(e).map((_,v2))
        }
      }
    }
  }
}
