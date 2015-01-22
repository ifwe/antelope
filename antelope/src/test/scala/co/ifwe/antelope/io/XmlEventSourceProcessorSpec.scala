package co.ifwe.antelope.io

import co.ifwe.antelope.Event
import org.scalatest.FlatSpec

import scala.language.postfixOps

class XmlEventSourceProcessorSpec extends FlatSpec {
  case class TestEvent(ts: Long, sku: String, name: String) extends Event { }
  "An XmlEventSourceProcessor" should "parse a simple product catalog" in {
    val url = this.getClass.getClassLoader.getResource("sample_product_data.xml")
    val events = XmlEventSourceProcessor[TestEvent]("product",node => {
      val sku = (node \ "sku" text)
      val name = (node \ "name" text)
      new TestEvent(0L, sku, name)
    }).getEvents(url).toArray
    assert(events.size === 57)
    assert(events.take(3) === List(
      TestEvent(0L, "1004622","Sniper: Ghost Warrior - Xbox 360"),
      TestEvent(0L, "1010544","Monopoly Streets - Xbox 360"),
      TestEvent(0L, "1011067","MySims: SkyHeroes - Xbox 360")
    ))
  }
}
