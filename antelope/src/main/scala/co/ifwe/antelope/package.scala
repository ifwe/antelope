package co.ifwe

import scala.reflect.ClassTag

package object antelope {
  type IdExtractor = SimpleUpdateDefinition[Long]

  implicit def SimpleUpdate2IterableUpdate[T:ClassTag](x: SimpleUpdateDefinition[T]) =
    new IterableUpdateDefinition[T] {
      override def getFunction: PartialFunction[Event, Iterable[T]] = x.getFunction andThen (Array[T](_))
    }

}
