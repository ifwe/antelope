package co.ifwe.antelope.bestbuy

import co.ifwe.antelope.Evaluation
import co.ifwe.antelope.bestbuy.event.ProductView

class BestBuyEvaluation(val pv: ProductView, val topDocs: Array[Long]) extends Evaluation