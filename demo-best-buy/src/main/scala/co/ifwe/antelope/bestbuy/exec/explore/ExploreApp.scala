package co.ifwe.antelope.bestbuy.exec.explore

import java.io.{File, FileWriter, PrintWriter}

import co.ifwe.antelope.bestbuy.event.Storage
import co.ifwe.antelope.bestbuy.exec.explore.state.Counter
import co.ifwe.antelope.util.ProgressMeter

trait ExploreApp extends App {
  private val trainingDir = "/Users/johann/dev/aml/bestbuy/training_large"
  private val viewsFn = "/Users/johann/dev/aml/bestbuy/data_large/train_sorted.csv"
  private val viewsFnBin = viewsFn + ".bin"

  private val startTime = System.currentTimeMillis()
  println(s"starting tests at ${System.currentTimeMillis() - startTime}")
  private val pm = new ProgressMeter(printInterval = 50000)

  val events = Storage.readEvents(viewsFnBin).view.map(e => {
    pm.increment()
    e
  })

  def save[K](titles: Iterable[String], c: Counter[K])(implicit ord: Ordering[K]): Unit = {
    val t = titles.toArray
    val fn = trainingDir + File.separatorChar + t.map(_.replaceAll(" ","-")).mkString("_") + ".csv"
    val w = new PrintWriter(new FileWriter(fn))
    try {
      w.println(t.mkString(","))
      c.toMap().toArray.sortBy(_._1).map { case (k, v) => k + "," + v }.foreach(w.println)
    } finally {
      w.close()
    }
  }

  def printTiming(): Unit = {
    pm.finished()
  }

}
