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

package com.typesafe.config.impl

import java.time.Duration

import com.typesafe.config.{ ConfigMemorySize, ConfigValueType }
import io.circe.Decoder

import scala.concurrent.duration.FiniteDuration
import scala.util.{ Failure, Success, Try }

/**
  * Created by vladimir on 23/11/16.
  */
trait ConfigDecoders {

  implicit val doubleInstance: Decoder[Double] =
    Decoder.decodeDouble.handleErrorWith(t =>
      Decoder.decodeString.emapTry { str =>
        val transformed =
          DefaultTransformer.transform(
            new ConfigString.Quoted(SimpleConfigOrigin.newSimple(s"Decode number from $str"), str),
            ConfigValueType.NUMBER)
        if (transformed.valueType() == ConfigValueType.NUMBER)
          Success(transformed.asInstanceOf[ConfigNumber].doubleValue())
        else Failure(t)
    })

  implicit val durationDecoder: Decoder[Duration] =
    Decoder.decodeString.emapTry { s =>
      Try(SimpleConfig.parseDuration(s, SimpleConfigOrigin.newSimple(s"Parse duration from $s"), ""))
        .map(Duration.ofNanos)
    }.or(Decoder.decodeLong.map(Duration.ofMillis))

  private[this] val memSizeFromStringDecoder: Decoder[ConfigMemorySize] =
    Decoder.decodeString.emapTry { s =>
      Try(SimpleConfig.parseBytes(s, SimpleConfigOrigin.newSimple("memSizeFromStringDecoder"), ""))
        .map(ConfigMemorySize.ofBytes)
    }
  implicit val memorySizeDecoder: Decoder[ConfigMemorySize] = {
    Decoder.decodeLong.map(ConfigMemorySize.ofBytes).or(memSizeFromStringDecoder)
  }

  import scala.concurrent.duration.MILLISECONDS
  implicit val finiteDurationDecoder: Decoder[FiniteDuration] =
    durationDecoder.map(x => FiniteDuration(x.toMillis, MILLISECONDS))
}
