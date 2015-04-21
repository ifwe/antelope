package co.ifwe.antelope.io

import java.net.URL

import co.ifwe.antelope.Event

/**
 * The CachedEventConfiguration provides high-performance access to input EventHistory
 * By merging multiple data sources and by serializing events in the efficient
 * Kryo format it makes practical interactive replay even for data sets with millions
 * of input events.
 */
trait CachedEventConfiguration extends EventConfiguration {
  def storage(): KryoEventStorage
  def cacheDir(): String
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
