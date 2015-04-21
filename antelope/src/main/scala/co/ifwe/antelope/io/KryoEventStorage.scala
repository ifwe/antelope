package co.ifwe.antelope.io

import java.io.{BufferedInputStream, BufferedOutputStream, OutputStream, InputStream}

import co.ifwe.antelope.Event
import co.ifwe.antelope.util.CustomIterator
import com.esotericsoftware.kryo.KryoException
import com.twitter.chill.ScalaKryoInstantiator
import com.esotericsoftware.kryo.io.{Input, Output}

/**
 * Event storage using the Kryo serialization protocol. Note that
 * this implementation requires registration, e.g. as,
 * {{{
 *   new KryoEventStorage {
 *     kryo.register(classOf[ProductView])
 *     kryo.register(classOf[ProductUpdate])
 *     kryo.register(classOf[Array[String]])
 *   }
 * }}}
 *
 */
trait KryoEventStorage extends EventStorage {
  private val instantiator = new ScalaKryoInstantiator
  protected val kryo = instantiator.newKryo()

  // demanding performance so only serialize registered types
  kryo.setRegistrationRequired(true)

  override def readEvents(is: InputStream): Iterable[Event] = {
    new CustomIterator[Event] {
      var input: Input = _
      override def init(): Unit = {
        input = new Input(new BufferedInputStream(is))
      }
      override def advance(): Event = {
        try {
          kryo.readClassAndObject(input).asInstanceOf[Event]
        } catch {
          case e: KryoException =>
            input.close()
            null
        }
      }
    }.toIterable
  }

  override def getEventWriter(os: OutputStream) = new EventWriter {
    val output = new Output(new BufferedOutputStream(os))
    override def write(e: Event): Unit = {
      kryo.writeClassAndObject(output, e)
    }
    override def close(): Unit = {
      output.close()
    }
  }
}
