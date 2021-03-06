package com.github.plokhotnyuk.jsoniter_scala.macros

import java.nio.charset.StandardCharsets._

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros.CirceEncodersDecoders._
import com.github.plokhotnyuk.jsoniter_scala.macros.DslPlatformJson._
import com.github.plokhotnyuk.jsoniter_scala.macros.JacksonSerDesers._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsoniterCodecs._
import io.circe.parser._
import io.circe.syntax._
import org.openjdk.jmh.annotations.{Benchmark, Param, Setup}
import play.api.libs.json.Json

import scala.collection.breakOut

class ArrayOfFloatsBenchmark extends CommonParams {
  @Param(Array("1", "10", "100", "1000", "10000", "100000", "1000000"))
  var size: Int = 10
  var obj: Array[Float] = _
  var jsonString: String = _
  var jsonBytes: Array[Byte] = _

  setup()

  @Setup
  def setup(): Unit = {
    obj = (1 to size)
      .map(i => (((i * 1498724053) / Math.pow(10, i % 10)).toInt * Math.pow(10, (i % 20) - 10)).toFloat)(breakOut)
    jsonString = obj.mkString("[", ",", "]")
    jsonBytes = jsonString.getBytes(UTF_8)
    preallocatedBuf = new Array[Byte](jsonBytes.length + preallocatedOff + 100/*to avoid possible out of bounds error*/)
  }

  @Benchmark
  def readCirce(): Array[Float] = decode[Array[Float]](new String(jsonBytes, UTF_8)).fold(throw _, x => x)

  @Benchmark
  def readDslJsonJava(): Array[Float] = decodeDslJson[Array[Float]](jsonBytes)

  @Benchmark
  def readJacksonScala(): Array[Float] = jacksonMapper.readValue[Array[Float]](jsonBytes)

  @Benchmark
  def readJsoniterScala(): Array[Float] = readFromArray[Array[Float]](jsonBytes)

  @Benchmark
  def readPlayJson(): Array[Float] = Json.parse(jsonBytes).as[Array[Float]]

  @Benchmark
  def writeCirce(): Array[Byte] = printer.pretty(obj.asJson).getBytes(UTF_8)

  @Benchmark
  def writeDslJsonJava(): Array[Byte] = encodeDslJson[Array[Float]](obj)

  @Benchmark
  def writeJacksonScala(): Array[Byte] = jacksonMapper.writeValueAsBytes(obj)

  @Benchmark
  def writeJsoniterScala(): Array[Byte] = writeToArray(obj)

  @Benchmark
  def writeJsoniterScalaPrealloc(): Int = writeToPreallocatedArray(obj, preallocatedBuf, preallocatedOff)
/* FIXME: Play-JSON serialize BigDecimal values instead of float
  @Benchmark
  def writePlayJson(): Array[Byte] = Json.toBytes(Json.toJson(obj))
*/
}