package co.ifwe.antelope.bestbuy.event

import co.ifwe.antelope.bestbuy.Product
import co.ifwe.antelope.{util, Event}

/**
 * Event-based representation of Best Buy product catalog
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 * @param ts timestamp
 * @param sku product identifier
 * @param name product name (game title)
 * @param description (product description text)
 */
class ProductUpdate(val ts: Long, sku: Long, name: String, description: String)
  extends Product(sku, name, description) with Event {
  protected def shortDesc: String = {
    if (description.length > 40) {
      description.substring(0,37) + "..."
    } else {
      description
    }
  }
  override def toString: String= {
    s"${util.formatTimestamp(ts)}:$sku:$name:${shortDesc}"
  }
}

object ProductUpdate {
  def apply(ts: Long, product: Product) = {
    new ProductUpdate(ts, product.sku, product.name, product.description)
  }
}

