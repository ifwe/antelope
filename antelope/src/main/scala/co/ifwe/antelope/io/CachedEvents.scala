package co.ifwe.antelope.io

import java.io._

import co.ifwe.antelope.Event
import co.ifwe.antelope.util.{CustomIterator, Digest}

/**
  * Cache management code
  *
  * @param cacheDir
  * @param storage
  */
class CachedEvents(cacheDir: String, storage: EventStorage) {
  def apply[T<:Event](identifier: String, events: () => Iterable[T]): Iterable[T] = {
    val idHash = Digest.sha1(identifier)
    val cacheFn = idHash.map(b => Integer.toString((b & 0xff) + 0x100, 16).substring(1)).mkString + ".cache.bin"
    println(s"cached file path: $cacheFn")
    val f = new File(cacheDir + File.separatorChar + cacheFn)
    if (f.exists()) {
      // note that in this asInstanceOf call we are assuming that not types are changed during
      // serialization
      println(s"getting events for $identifier from cache")
      println(s"$storage")
      storage.readEvents(new BufferedInputStream(new FileInputStream(f))).asInstanceOf[Iterable[T]]
    } else {
      println(s"events for $identifier not found in cache")
      // pass the event through but write to cache as we go
      val fInitial = new File(cacheDir + File.separatorChar + cacheFn + ".inprogress")
      val w = storage.getEventWriter(new BufferedOutputStream(new FileOutputStream(fInitial)))
      new CustomIterator[T] {
        var it: Iterator[T] = _
        override def init: Unit = {
          it = events().iterator
        }
        override def advance(): T = {
          if (it.hasNext) {
            val next = it.next
            w.write(next)
            next
          } else {
            w.close()
            fInitial.renameTo(f)
            null.asInstanceOf[T]
          }
        }
      }.toIterable
    }
  }
}
