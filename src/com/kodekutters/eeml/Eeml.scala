package com.kodekutters.eeml

import javax.xml.datatype.XMLGregorianCalendar

/*
* Copyright (c) 2013, Ringo Wathelet
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*
* - Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
*
* - Redistributions in binary form must reproduce the above copyright notice, this
*   list of conditions and the following disclaimer in the documentation and/or
*   other materials provided with the distribution.
*
* - Neither the name of "scalaEeml" nor the names of its contributors may
*   be used to endorse or promote products derived from this software without
*   specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

case class Location(name: Option[String] = None,
                    lat: Option[Double],
                    lon: Option[Double],
                    ele: Option[Double] = None,
                    exposure: Option[String] = None,
                    domain: Option[String],
                    disposition: Option[String] = None) {

  def this(name: String, lat: Double, lon: Double, ele: Double, exposure: String, domain: String, disposition: String) =
    this(Option(name), Option(lat), Option(lon), Option(ele), Option(exposure), Option(domain), Option(disposition))

}

case class DataValue(value: Option[String], at: Option[XMLGregorianCalendar] = None) {
  def this(value: String, at: XMLGregorianCalendar) = this(Option(value), Option(at))
}

trait TypeOfUnit

object TypeOfUnit {
  def fromString(value: String): TypeOfUnit = value match {
    case "basicSI" => BasicSI
    case "derivedSI" => DerivedSI
    case "conversionBasedUnits" => ConversionBasedUnits
    case "derivedUnits" => DerivedUnits
    case "contextDependentUnits" => ContextDependentUnits
  }
}

case object BasicSI extends TypeOfUnit { override def toString = "basicSI" }
case object DerivedSI extends TypeOfUnit { override def toString = "derivedSI" }
case object ConversionBasedUnits extends TypeOfUnit { override def toString = "conversionBasedUnits" }
case object DerivedUnits extends TypeOfUnit { override def toString = "derivedUnits" }
case object ContextDependentUnits extends TypeOfUnit { override def toString = "contextDependentUnits" }

case class UnitType(value: Option[String], symbol: Option[String] = None, typeValue: Option[TypeOfUnit] = None) {
  def this(value: String, symbol: String, typeValue: TypeOfUnit) = this(Option(value), Option(symbol), Option(typeValue))
}

case class Data(tag: Seq[String] = Seq.empty,
                current_value: Option[DataValue],
                max_value: Option[String] = None,
                min_value: Option[String] = None,
                datapoints: Seq[DataValue] = Seq.empty,
                unit: Option[UnitType] = None,
                id: Option[BigInt]) {

  def this(tag: Seq[String], current_value: DataValue,
           max_value: String, min_value: String, datapoints: Seq[DataValue],
           unit: UnitType, id: BigInt) = this(tag, Option(current_value), Option(max_value),
    Option(min_value), datapoints, Option(unit), Option(id))

}

case class Environment(title: Option[String] = None,
                       feed: Option[String] = None,
                       status: Option[String] = None,
                       privateValue: Option[Boolean] = None,
                       description: Option[String] = None,
                       icon: Option[String] = None,
                       website: Option[String] = None,
                       email: Option[String] = None,
                       tag: Seq[String] = Seq.empty,
                       location: Option[Location] = None,
                       data: Seq[Data] = Seq.empty,
                       updated: Option[XMLGregorianCalendar] = None,
                       creator: Option[String] = None,
                       id: Option[BigInt] = None) {

  def this(title: String, feed: String, status: String, privateValue: Boolean, description: String,
           icon: String, website: String, email: String, tag: Seq[String],
           location: Location, data: Seq[Data], updated: XMLGregorianCalendar,
           creator: String, id: BigInt) = this(Option(title), Option(feed), Option(status),
    Option(privateValue), Option(description), Option(icon), Option(website), Option(email),
    tag, Option(location), data, Option(updated), Option(creator), Option(id))
}

case class Eeml(environments: Seq[Environment] = Seq.empty, version: Option[String] = Some("0.5.1")) {

  def this(environment: Environment) = this(Seq(environment))

  def this(environment: Environment, version: String) = this(Seq(environment), Option(version))

}
