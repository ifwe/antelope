package co.ifwe.antelope

trait UpdateDefinition[T] {
  def getFunction: PartialFunction[Event,T]
}

object UpdateDefinition {
  def defUpdate[T](x: PartialFunction[Event,T]): SimpleUpdateDefinition[T] = {
    new SimpleUpdateDefinition[T](x)
  }

  def defVectorUpdate[T](x: PartialFunction[Event,Iterable[T]]) =
    new IterableUpdateDefinition[T] {
      override def getFunction: PartialFunction[Event, Iterable[T]] = x
    }
}