package co.ifwe.antelope

import scala.reflect.ClassTag

class SimpleUpdateDefinition[T](f: PartialFunction[Event, T]) extends UpdateDefinition[T] {
  override def getFunction: PartialFunction[Event, T] = f

  def *[U](x: IterableUpdateDefinition[U]) = {
    new IterableUpdateDefinition[(T,U)] {
      override def getFunction = new PartialFunction[Event, Iterable[(T, U)]] {
        override def isDefinedAt(e: Event): Boolean = f.isDefinedAt(e) && x.getFunction.isDefinedAt(e)

        override def apply(e: Event): Iterable[(T, U)] = {
          val v1 = f(e)
          x.getFunction(e).map((v1,_))
        }
      }
    }
  }

  implicit def SimpleUpdate2IterableUpdate[T:ClassTag](x: SimpleUpdateDefinition[T]) =
    new IterableUpdateDefinition[T] {
      override def getFunction: PartialFunction[Event, Iterable[T]] = x.getFunction andThen (Array[T](_))
    }
}
