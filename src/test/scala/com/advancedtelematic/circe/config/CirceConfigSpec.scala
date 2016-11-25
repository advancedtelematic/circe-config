/*
 * Copyright 2016 ATS Advanced Telematic Systems GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.advancedtelematic.circe.config

import java.time.Duration

import com.typesafe.config.impl.ConfigDecoders
import com.typesafe.config.{ ConfigFactory, ConfigMemorySize }
import org.scalatest.{ FlatSpec, Matchers }

/**
  * Created by vladimir on 23/11/16.
  */
class CirceConfigSpec extends FlatSpec with Matchers with ConfigDecoders {

  val conf = ConfigFactory.load("test01")
  val json = parser.parseConfig(conf).right.get

  it should "decode ints" in {
    val ints = json.hcursor.downField("ints")
    ints.downField("fortyTwo").as[Int] should equal(Right(42))
    ints.get[Int]("fortyTwoAgain") should equal(Right(42))
    ints.get[Long]("fortyTwoAgain") should equal(Right(42L))
  }

  it should "decode doubles" in {

    val floats = json.hcursor.downField("floats")
    floats.get[Double]("fortyTwoPointOne") should equal(Right(42.1))
    floats.get[Double]("fortyTwoPointOneAgain") should equal(Right(42.1))
    floats.get[Double]("pointThirtyThree") should equal(Right(0.33))
    floats.get[Double]("pointThirtyThreeAgain") should equal(Right(0.33))
  }

  it should "decode strings" in {
    val strings = json.hcursor.downField("strings")
    strings.get[String]("abcd") should equal(Right("abcd"))
    strings.get[String]("abcdAgain") should equal(Right("abcd"))
    strings.get[String]("concatenated") should equal(Right("null bar 42 baz true 3.14 hi"))
  }

  it should "decode booleans" in {
    val booleans = json.hcursor.downField("booleans")
    booleans.get[Boolean]("trueAgain") should equal(Right(true))
    booleans.get[Boolean]("falseAgain") should equal(Right(false))
  }

  it should "decode null" in {
    json.hcursor.downField("nulls").downField("null").focus.map(_.isNull) should equal(Some(true))
  }

  it should "decode arrays" in {
    val arrays = json.hcursor.downField("arrays")
    arrays.get[Seq[Int]]("empty") should equal(Right(Seq()))
    arrays.get[Seq[Int]]("ofInt") should equal(Right(Seq(1, 2, 3)))
    arrays.get[Seq[Long]]("ofInt") should equal(Right(Seq(1L, 2L, 3L)))
    arrays.get[Seq[String]]("ofString") should equal(Right(Seq("a", "b", "c")))
    arrays.get[Seq[Double]]("ofDouble") should equal(Right(Seq(3.14, 4.14, 5.14)))
    arrays.get[Seq[Boolean]]("ofBoolean") should equal(Right(Seq(true, false)))
    arrays.get[Seq[Seq[String]]]("ofArray") should equal(
      Right(Seq(Seq("a", "b", "c"), Seq("a", "b", "c"), Seq("a", "b", "c"))))
  }

  it should "decode duration" in {
    import cats.syntax.either._
    val durations = json.hcursor.downField("durations")
    durations.get[Duration]("second").map(_.toMillis) should equal(Right(1000L))
    durations.get[Duration]("secondAsNumber").map(_.toMillis) should equal(Right(1000L))
    durations.get[Seq[Duration]]("secondsList").map(_.map(_.toMillis)) should equal(
      Right(Seq(1000L, 2000L, 3000L, 4000L)))
    durations.get[Duration]("halfSecond").map(_.toMillis) should equal(Right(500L))
    durations.get[Duration]("largeNanos").map(_.toNanos) should equal(Right(4878955355435272204L))
    durations.get[Duration]("plusLargeNanos").map(_.toNanos) should equal(Right(4878955355435272204L))
    durations.get[Duration]("minusLargeNanos").map(_.toNanos) should equal(Right(-4878955355435272204L))
  }

  it should "decode memory size in bytes" in {
    val memsizes = json.hcursor.downField("memsizes")
    memsizes.get[ConfigMemorySize]("meg") should equal(Right(ConfigMemorySize.ofBytes(1024 * 1024L)))
    memsizes.get[ConfigMemorySize]("megAsNumber") should equal(Right(ConfigMemorySize.ofBytes(1024 * 1024L)))
    memsizes.get[ConfigMemorySize]("halfMeg") should equal(Right(ConfigMemorySize.ofBytes(512 * 1024L)))
  }
}
