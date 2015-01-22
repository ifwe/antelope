package co.ifwe.antelope.io

import java.io._
import java.net.URL

import co.ifwe.antelope.util.{CustomIterator, Digest}
import co.ifwe.antelope.{EventSourceProcessor, Event}

trait CachedEventConfiguration extends EventConfiguration {
  val cacheDir = "/Users/johann/tmp/antelope"
  val storage: KryoEventStorage

  protected def cachedProcessor[T<:Event](ep: EventSourceProcessor[T]) = new EventSourceProcessor[T] {
    override def getEvents(url: URL): Iterable[T] = {
      val idStr = s"${url.getProtocol}:${url.getHost}:${url.getFile}:${url.getPort}:${url.getRef}"
      val idHash = Digest.sha1(idStr)
      val cacheFn = idHash.map(b => Integer.toString((b & 0xff) + 0x100, 16).substring(1)).mkString + ".cache.bin"
      println(s"cached file path: $cacheFn")
      val f = new File(cacheDir + File.separatorChar + cacheFn)
      if (f.exists()) {
        // note that in this asInstanceOf call we are assuming that not types are changed during
        // serialization
        println(s"getting events for $url from cache")
        storage.readEvents(new BufferedInputStream(new FileInputStream(f))).asInstanceOf[Iterable[T]]
      } else {
        println(s"events for $url not found in cache")
        // pass the event through but write to cache as we go
        val fInitial = new File(cacheDir + File.separatorChar + cacheFn + ".inprogress")
        val w = storage.getEventWriter(new BufferedOutputStream(new FileOutputStream(fInitial)))
        new CustomIterator[T] {
          var it: Iterator[T] = _
          override def init: Unit = {
            it = ep.getEvents(url).iterator
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
  override def events: Iterable[Event] = {
    processors.map{case (url,processor) => cachedProcessor(processor).getEvents(url)}.flatten.sortBy(_.ts)
  }
}
