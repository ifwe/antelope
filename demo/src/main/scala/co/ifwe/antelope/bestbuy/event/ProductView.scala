package co.ifwe.antelope.bestbuy.event

import co.ifwe.antelope.{HasQuery, Event, util}

/**
 * Event-based representation of Best Buy user activity as from the
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 * @param ts
 * @param query
 * @param skuSelected
 */
case class ProductView(ts: Long, query: String, skuSelected: Long) extends Event with HasQuery {
  override def toString() = {
    s"${util.formatTimestamp(ts)}:$query->$skuSelected"
  }
}
