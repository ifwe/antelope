package co.ifwe.antelope.bestbuy

import java.io.File

import co.ifwe.antelope.UpdateDefinition._
import co.ifwe.antelope._
import co.ifwe.antelope.bestbuy.event.{ProductUpdate, ProductView}
import co.ifwe.antelope.io.WeightsReader
import co.ifwe.antelope.model.AllDocs
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.slf4j.LoggerFactory

import scala.collection.mutable

class BestBuyDemoServlet extends AntelopeBestBuyDemoStack with JacksonJsonSupport with EventProcessing {
  val logger = LoggerFactory.getLogger(getClass)

  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal

  val catalog = mutable.HashMap[Long, ProductUpdate]()

  trait Suggestions {
    def suggest(query: String, limit: Int): Array[String]
  }

  val suggestModel = new Model[ProductSearchScoringContext] with Suggestions {
    import co.ifwe.antelope.Text.normalize
    import s._

    val queryPopularityCounter = stringPrefixCounter(defUpdate {
      case pv: ProductView => normalize(pv.query)
    })

    def suggest(query: String, limit: Int): Array[String] = {
      queryPopularityCounter.prefixSearch(normalize(query)).toArray.sortBy(_._2).map(_._1).take(limit)
    }
  }

  val weights = WeightsReader.getWeights(new File(weightsFn).toURI.toURL)
  val allDocs = new AllDocs
  val ranker = new Ranker(model, weights, allDocs)

  var eventCt = 0
  eventHistory.getEvents(Long.MinValue, Long.MaxValue, _ => true, (e: Event) => {
    // TODO skip duplicate product updates <= make this part of the model
    model.update(e)
    suggestModel.update(e)
    e match {
      case pu: ProductUpdate =>
        allDocs.addDoc(pu.sku)
        catalog += pu.sku -> pu
      case _ =>
    }
    eventCt += 1
    true
  })
  logger.info(s"updated $eventCt events")

  case class Query(query: String)
  case class Results(success: Boolean, data: Array[Long])
  case class ProductDescription(sku: Long, title: String)
  case class ProductDetailedDescription(sku: Long, title: String, description: String)
  case class ResultsWithTitles(success: Boolean, results: Array[ProductDescription],
                                inferredQuery: String)

  private def buildResults(ctx: ProductSearchScoringContext, results: TopDocsResult[ProductSearchScoringContext]) : ResultsWithTitles = {
    new ResultsWithTitles(success = true,
      results = results.topDocs.map(sku => ProductDescription(sku, catalog(sku).name)),
      inferredQuery = if (ctx.query == results.executedCtx.query) null else results.executedCtx.query
    )
  }

  before() {
    contentType = formats("json")
  }

  get("/details") {
    val sku = params("sku").toLong
    logger.info(s"getting detail for $sku")
    val desc = catalog(sku)
    new ProductDetailedDescription(sku, desc.name, desc.description)
  }

  get("/suggest") {
    val query = params("query")
    logger.info(s"getting completion prediction for $query")
    try {
      suggestModel.suggest(query, 10).toList
    } catch {
      case e: Exception =>
        logger.error("problem in suggestions", e)
        throw(e)
    }
  }

  post("/search") {
    val queryStr = params("query")
    logger.info(s"doing a search for '$queryStr'")
    val ctx = new ProductSearchScoringContext {
      override val t = Long.MaxValue
      override val query = queryStr
    }
    val results = ranker.topN(ctx, 10)
    logger.info(s"number of results is ${results.topDocs.size}")
    buildResults(ctx, results)
  }
  
}
