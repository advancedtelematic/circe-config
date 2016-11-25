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

import com.typesafe.config.{
  ConfigList,
  ConfigObject,
  ConfigValue,
  ConfigValueType
}
import io.circe.Json

/**
  * Created by vladimir on 23/11/16.
  */
private[parser] object ConfigValueConverters {

  def configValueToJson(configValue: ConfigValue): Json =
    configValue.valueType() match {
      case ConfigValueType.OBJECT =>
        configObjectToJson(configValue.asInstanceOf[ConfigObject])

      case ConfigValueType.BOOLEAN =>
        Json.fromBoolean(configValue.unwrapped().asInstanceOf[Boolean])

      case ConfigValueType.LIST =>
        configListToJson(configValue.asInstanceOf[ConfigList])

      case ConfigValueType.NULL => Json.Null

      case ConfigValueType.NUMBER =>
        val configNumber = configValue.unwrapped()
        configNumber match {
          case x: Integer          => Json.fromInt(x)
          case x: java.lang.Long   => Json.fromLong(x)
          case x: java.lang.Double => Json.fromDoubleOrString(x)
        }

      case ConfigValueType.STRING =>
        Json.fromString(configValue.unwrapped().asInstanceOf[String])
    }

  private[this] def configListToJson(configList: ConfigList): Json = {
    import scala.collection.JavaConverters._
    Json.arr(configList.asScala.map(configValueToJson): _*)
  }

  private[this] def configObjectToJson(configObj: ConfigObject): Json = {
    import scala.collection.JavaConverters._
    Json.obj(configObj.asScala.mapValues(configValueToJson).toArray: _*)
  }
}
