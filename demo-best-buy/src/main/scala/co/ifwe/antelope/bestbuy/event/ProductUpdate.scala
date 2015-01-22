package co.ifwe.antelope.bestbuy.event

import co.ifwe.antelope.bestbuy.Product
import co.ifwe.antelope.{Event, util}

/**
 * Event-based representation of Best Buy product catalog
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 * @param ts timestamp
 * @param sku product identifier
 * @param name product name (game title)
 * @param description (product description text)
 */
case class ProductUpdate(ts: Long, override val sku: Long, override val name: String, override val description: String, override val categories: Array[String])
  extends Product(sku, name, description, categories) with Event {
  protected def shortDesc: String = {
    if (description.length > 40) {
      description.substring(0,37) + "..."
    } else {
      description
    }
  }
  override def toString: String= {
    s"${util.formatTimestamp(ts)}:$sku:$name:${categories.mkString(",")}:${shortDesc}"
  }
  override def equals(o: Any) = {
    // TODO could probably simply this by not extending Product and just having one case class
    if (o.isInstanceOf[ProductUpdate]) {
      val op = o.asInstanceOf[ProductUpdate]
      this.ts == op.ts && this.sku == op.sku && this.name == op.name && this.description == op.description &&
        this.categories.size == op.categories.size && this.categories.zip(op.categories).map(x => x._1 == x._2).reduce((a,b) => a && b)
    } else {
      false
    }
  }
}

object ProductUpdate {
  def apply(ts: Long, product: Product) = {
    new ProductUpdate(ts, product.sku, product.name, product.description, product.categories)
  }
}

