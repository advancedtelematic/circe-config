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

import com.typesafe.config.impl.ConfigDecoders
import io.circe.{ Decoder, HCursor }

/**
  * Created by vladimir on 23/11/16.
  */
object Decoders extends ConfigDecoders {

  implicit val BooleanDecoder: Decoder[Boolean] = Decoder.instance { (c: HCursor) =>
    import cats.syntax.either._
    c.as[Boolean](Decoder.decodeBoolean) match {
      case x @ Right(_) => x
      case x @ Left(_) =>
        c.as[String]
          .flatMap {
            case "true"  => Right(true)
            case "false" => Right(false)
            case _       => x
          }
          .left
          .flatMap(_ => x)
    }
  }
}
