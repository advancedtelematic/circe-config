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

package com.advancedtelematic.circe.config.parser

import cats.data.{ NonEmptyList, Validated, ValidatedNel }
import com.typesafe.config._
import io.circe.{ Decoder, Json, Parser, ParsingFailure }

import scala.util.{ Failure, Success, Try }

class ConfigParser extends Parser {
  type ParsingResult = Either[ParsingFailure, Json]

  override def parse(input: String): ParsingResult =
    Try(ConfigFactory.parseString(input)) match {
      case Success(cfg) => parseConfig(cfg)
      case Failure(t)   => Left(ParsingFailure(t.getMessage, t))
    }

  def parseConfig(config: Config): ParsingResult =
    Right(ConfigValueConverters.configValueToJson(config.root()))

  final def decodeConfig[A](config: Config)(implicit decoder: Decoder[A]): Either[io.circe.Error, A] =
    parseConfig(config) match {
      case Right(json) => decoder.decodeJson(json)
      case l @ Left(_) => l.asInstanceOf[Either[io.circe.Error, A]]
    }

  def decodeConfigAccumulating[A](config: Config)(implicit decoder: Decoder[A]): ValidatedNel[io.circe.Error, A] =
    parseConfig(config) match {
      case Right(json) =>
        decoder.decodeAccumulating(json.hcursor).leftMap {
          case NonEmptyList(h, t) => NonEmptyList(h, t)
        }
      case Left(error) => Validated.invalidNel(error)
    }

}
