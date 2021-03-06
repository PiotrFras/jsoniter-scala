package com.github.plokhotnyuk.jsoniter_scala.macros

import java.nio.charset.StandardCharsets.UTF_8

import com.github.plokhotnyuk.jsoniter_scala.core._
//import com.github.plokhotnyuk.jsoniter_scala.macros.CirceEncodersDecoders._
import com.github.plokhotnyuk.jsoniter_scala.macros.JacksonSerDesers._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsoniterCodecs._
import com.github.plokhotnyuk.jsoniter_scala.macros.PlayJsonFormats._
//import io.circe.generic.auto._
//import io.circe.parser._
//import io.circe.syntax._
import org.openjdk.jmh.annotations.Benchmark
import play.api.libs.json.Json

import scala.collection.immutable.BitSet

class BitSetBenchmark extends CommonParams {
  val obj: BitSet = BitSet(0 to 127: _*)
  val jsonString: String = obj.mkString("[", ",", "]")
  val jsonBytes: Array[Byte] = jsonString.getBytes(UTF_8)

/* FIXME: Circe doesn't support parsing of bitsets
  @Benchmark
  def readCirce(): BitSet = decode[BitSet](new String(jsonBytes, UTF_8)).fold(throw _, x => x)
*/
/* FIXME: Jackson throws java.lang.IllegalArgumentException: Need exactly 1 type parameter for collection like types (scala.collection.immutable.BitSet)
  @Benchmark
  def readJacksonScala(): BitSet = jacksonMapper.readValue[BitSet](jsonBytes)
*/
  @Benchmark
  def readJsoniterScala(): BitSet = readFromArray[BitSet](jsonBytes)

  @Benchmark
  def readPlayJson(): BitSet = Json.parse(jsonBytes).as[BitSet](bitSetFormat)

/* FIXME: Circe doesn't support writing of bitsets
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
  def writePlayJson(): Array[Byte] = Json.toBytes(Json.toJson(obj)(bitSetFormat))
}