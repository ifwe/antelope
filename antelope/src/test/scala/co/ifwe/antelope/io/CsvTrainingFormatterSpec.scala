package co.ifwe.antelope.io

import co.ifwe.antelope.TrainingExample
import org.scalatest.FlatSpec

class CsvTrainingFormatterSpec extends FlatSpec {
  "A CsvTrainingFormatter" should "format simple content" in {
    val f = new CsvTrainingFormatter(Array("column_a","column_b"))
    assert(f.header().get === "outcome,column_a,column_b")

    val e1 = new TrainingExample(true,Array(("column_a",1D),("column_b",2D)))
    assert(f.format(e1) === "1,1.0,2.0")

    val e2 = new TrainingExample(false,Array(("column_a",6D),("column_b",3D)))
    assert(f.format(e2) === "0,6.0,3.0")
  }
}
