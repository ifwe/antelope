package co.ifwe.antelope.io

import java.net.URL

import co.ifwe.antelope.Event


trait CachedEventConfiguration extends EventConfiguration {
  def storage(): KryoEventStorage
  val cacheDir: String
  val cache = new CachedEvents(cacheDir, storage)

  def getIdentifier(url: URL): String = {
    s"${url.getProtocol}:${url.getHost}:${url.getFile}:${url.getPort}:${url.getRef}"
  }

  override def events: Iterable[Event] = {
    val mergedCacheKey = processors.map{case (url, _) => getIdentifier(url)}.sorted.mkString("::")
    cache(mergedCacheKey,
      () => Merge.merge(processors.map{case (url,processor) => cache(getIdentifier(url), () => processor.getEvents(url).toArray[Event].sorted)})
    )
  }
}
