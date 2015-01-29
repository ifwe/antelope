package co.ifwe.antelope.io

import org.scalatest.FlatSpec

class WeightsReaderSpec extends FlatSpec {
  "A WeightsReader" should "read test weights" in {
    val url = this.getClass.getClassLoader.getResource("sample_weights.txt")
    val weights = WeightsReader.getWeights(url)
    assert(weights === Array(52.77964D,39.74343D,0.08017773D,-0.01137886D,0.8648198D,0.08873818D))
  }

  it should "read test weights including NA code" in {
    val url = this.getClass.getClassLoader.getResource("sample_weights_na.txt")
    val weights = WeightsReader.getWeights(url)
    assert(weights === Array(52.77964D,39.74343D,0.08017773D,0D,0.8648198D,0.08873818D))
  }
}
