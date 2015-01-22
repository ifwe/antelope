package co.ifwe.antelope.bestbuy.event

import java.io._

import co.ifwe.antelope.Event

import scala.language.existentials

import scala.reflect.runtime.universe._

import scala.collection.mutable.ArrayBuffer

object Storage {
  trait Serializer[T] {
    def write(output: DataOutput, data: T)
    def read(input: DataInput): T
  }

  class ProductViewSerializer extends Serializer[ProductView] {
    override def write(output: DataOutput, productView: ProductView): Unit = {
      output.writeLong(productView.ts)
      output.writeLong(productView.queryTs)
      output.writeLong(productView.user)
      output.writeLong(productView.skuSelected)
      output.writeUTF(productView.query)
    }

    override def read(input: DataInput): ProductView = {
      val ts = input.readLong()
      val queryTs = input.readLong()
      val user = input.readLong()
      val skuSelected = input.readLong()
      val query = input.readUTF()
      new ProductView(ts, queryTs, user, query, skuSelected)
    }
  }

  class ProductUpdateSerializer extends Serializer[ProductUpdate] {
    override def write(output: DataOutput, productUpdate: ProductUpdate): Unit = {
      output.writeLong(productUpdate.ts)
      output.writeLong(productUpdate.sku)
      output.writeUTF(productUpdate.name)
      output.writeUTF(productUpdate.description)
      output.writeInt(productUpdate.categories.length)
      productUpdate.categories.foreach(output.writeUTF)
    }

    override def read(input: DataInput): ProductUpdate = {
      val ts = input.readLong()
      val sku = input.readLong()
      val name = input.readUTF()
      val description = input.readUTF()
      val numCategories = input.readInt()
      val categories = new Array[String](numCategories)
      for (i <- 0 until numCategories) {
        categories(i) = input.readUTF()
      }
      new ProductUpdate(ts, sku, name, description, categories)
    }
  }
  val mirror = runtimeMirror(Storage.getClass.getClassLoader)
  var nextId: Short = 1
  val m = ArrayBuffer[(Type, Short, Serializer[_])]()
  register(new ProductViewSerializer())
  register(new ProductUpdateSerializer())

  def register[T](s: Serializer[T])(implicit tag: TypeTag[T]): Unit = {
    m.append((tag.tpe, nextId, s))
    nextId  = (nextId + 1).toShort
  }

  def write[T](output: DataOutput, x: T)(implicit tag: TypeTag[T]): Unit = {
    val rt = mirror.classSymbol(x.getClass).toType
    val (_,id, s) = m.find(_._1 == rt).get
    output.writeShort(id)
    s.asInstanceOf[Serializer[T]].write(output, x)
  }

  def read(input: DataInput) = {
    val id = input.readShort()
    val (_, _, s) = m.find(_._2 == id).get
    s.read(input)
  }

  def readEvents(fn: String): Iterable[Event] = {
    val r = new BufferedInputStream(new FileInputStream(fn), 65536)
    readEvents(r)
  }

  def readEvents(is: InputStream): Iterable[Event] = {
    val input = new DataInputStream(is)
    // TODO think about how to do exception handling properly here - perhaps return a closeable resource
    new Iterable[ProductView]() {
      override def iterator: Iterator[ProductView] = {
        new Iterator[ProductView] {
          var nextRes: ProductView = readNext()
          def readNext(): ProductView = {
            try {
              read(input).asInstanceOf[ProductView]
            } catch {
              case e: EOFException => {
                input.close()
                null
              }
            }
          }

          override def hasNext: Boolean = nextRes != null

          override def next(): ProductView = {
            val res = nextRes
            nextRes = readNext()
            res
          }
        }
      }
    }
  }

  def writeEvents(fn: String, events: Iterable[Event]): Unit = {
    val os = new BufferedOutputStream(new FileOutputStream(fn))
    try {
      writeEvents(os, events)
    } finally {
      os.close()
    }
  }

  def writeEvents(os: OutputStream, events: Iterable[Event]): Unit = {
    val output = new DataOutputStream(os)
    events.foreach { e =>
      write(output, e)
    }
    output.flush()
  }
}
