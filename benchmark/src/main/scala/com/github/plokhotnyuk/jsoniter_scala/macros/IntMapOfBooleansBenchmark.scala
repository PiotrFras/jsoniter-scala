package com.github.plokhotnyuk.jsoniter_scala.macros

import java.nio.charset.StandardCharsets._

import com.github.plokhotnyuk.jsoniter_scala.core._
//import com.github.plokhotnyuk.jsoniter_scala.macros.CirceEncodersDecoders._
import com.github.plokhotnyuk.jsoniter_scala.macros.JacksonSerDesers._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsoniterCodecs._
import com.github.plokhotnyuk.jsoniter_scala.macros.PlayJsonFormats._
//import io.circe.parser._
//import io.circe.syntax._
import org.openjdk.jmh.annotations.Benchmark
import play.api.libs.json.Json

import scala.collection.breakOut
import scala.collection.immutable.IntMap

class IntMapOfBooleansBenchmark extends CommonParams {
  val obj: IntMap[Boolean] = (1 to 128).map { i =>
    (((i * 1498724053) / Math.pow(10, i % 10)).toInt, ((i * 1498724053) & 1) == 0)
  }(breakOut)
  val jsonString: String = obj.map(e => "\"" + e._1 + "\":" + e._2).mkString("{", ",", "}")
  val jsonBytes: Array[Byte] = jsonString.getBytes(UTF_8)

/* FIXME: Circe doesn't support IntMap
  @Benchmark
  def readCirce(): IntMap[Boolean] = decode[IntMap[Boolean]](new String(jsonBytes, UTF_8)).fold(throw _, x => x)
*/
/* FIXME: Jackson throws java.lang.IllegalArgumentException: Need exactly 2 type parameters for map like types (scala.collection.immutable.IntMap)
  @Benchmark
  def readJacksonScala(): IntMap[Boolean] = jacksonMapper.readValue[IntMap[Boolean]](jsonBytes)
*/
  @Benchmark
  def readJsoniterScala(): IntMap[Boolean] = readFromArray[IntMap[Boolean]](jsonBytes)

  @Benchmark
  def readPlayJson(): IntMap[Boolean] = Json.parse(jsonBytes).as[IntMap[Boolean]](intMapOfBooleansFormat)
/* FIXME: Circe doesn't support IntMap
  @Benchmark
  def writeCirce(): Array[Byte] = printer.pretty(obj.asJson).getBytes(UTF_8)
*/
  @Benchmark
  def writeJacksonScala(): Array[Byte] = jacksonMapper.writeValueAsBytes(obj)

  @Benchmark
  def writeJsoniterScala(): Array[Byte] = writeToArray(obj)

  @Benchmark
  def writeJsoniterScalaPrealloc(): Int = writeToPreallocatedArray(obj, preallocatedBuf, preallocatedOff)

  @Benchmark
  def writePlayJson(): Array[Byte] = Json.toBytes(Json.toJson(obj)(intMapOfBooleansFormat))
}