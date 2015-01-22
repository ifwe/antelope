package co.ifwe.antelope.bestbuy.exec.explore

import FileLocations._

object MergeEventsWithUpdates extends App {
  Merge.merge(Array(mergedProducts, viewsFnBin), allEvents)
}
