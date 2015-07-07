package co.ifwe.antelope.bestbuy.event

import co.ifwe.antelope.{Event, util}

/**
 * Event-based representation of Best Buy product catalog
 * [[https://www.kaggle.com/c/acm-sf-chapter-hackathon-small SF Bay Area ACM Data Mining Kaggle Competition]].
 * @param ts timestamp
 * @param sku product identifier
 * @param name product name (game title)
 * @param description (product description text)
 */
case class ProductUpdate(ts: Long, val sku: Long, val name: String, val description: String, val categories: Array[String]) extends Event {
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
  val MISSING_PRODUCT = new ProductUpdate(0L, 0L, "NO PRODUCT", "", Array[String]())
}

