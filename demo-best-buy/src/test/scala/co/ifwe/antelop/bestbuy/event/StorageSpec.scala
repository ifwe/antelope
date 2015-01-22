package co.ifwe.antelop.bestbuy.event

import java.io.{DataOutputStream, DataInputStream, ByteArrayInputStream, ByteArrayOutputStream}

import co.ifwe.antelope.bestbuy.event.{ProductView, Storage}
import org.scalatest.FlatSpec

class StorageSpec extends FlatSpec {
  "A Storage" should "serialize a simple event" in {
    val os = new ByteArrayOutputStream()
    val event = new ProductView(1L, 2L, 3L, "test", 4L)
    Storage.write(new DataOutputStream(os), event)
    os.close()

    val is = new ByteArrayInputStream(os.toByteArray)
    val eventRead = Storage.read(new DataInputStream(is))
    is.close
    assert(eventRead === event)
  }

  it should "serialize a list of events" in {
    val events = List(
      new ProductView(1L, 2L, 3L, "test", 4L),
      new ProductView(7L, 8L, 3L, "test2", 1L)
    )
    val os = new ByteArrayOutputStream()
    Storage.writeEvents(new DataOutputStream(os), events)
    os.close()

    val is = new ByteArrayInputStream(os.toByteArray)
    val eventsRead = Storage.readEvents(new DataInputStream(is)).toList
    is.close
    assert(eventsRead.size === 2)
    assert(eventsRead === events)
  }
}
