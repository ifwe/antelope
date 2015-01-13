package co.ifwe.antelope.bestbuy

object Product {
  val MISSING_PRODUCT = new Product(0L, "NO PRODUCT", "", Array[String]())
}

/**
 * Catalog product item
 * @param sku
 * @param name
 * @param description
 */
class Product(
  val sku: Long,
  val name: String,
  val description: String,
  val categories: Array[String]
) { }
