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

trait EemlObject

case class Location(name: Option[String] = None,
                    lat: Option[Double],
                    lon: Option[Double],
                    ele: Option[Double] = None,
                    exposure: Option[String] = None,
                    domain: Option[String],
                    disposition: Option[String] = None) extends EemlObject {

  def this(name: String, lat: Double, lon: Double, ele: Double, exposure: String, domain: String, disposition: String) =
    this(Option(name), Option(lat), Option(lon), Option(ele), Option(exposure), Option(domain), Option(disposition))
}

case class DataValue(value: Option[String], at: Option[XMLGregorianCalendar] = None) extends EemlObject {
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

case class UnitType(value: Option[String], symbol: Option[String] = None, typeValue: Option[TypeOfUnit] = None) extends EemlObject {
  def this(value: String, symbol: String, typeValue: TypeOfUnit) = this(Option(value), Option(symbol), Option(typeValue))
}

case class Data(tags: Option[Seq[String]] = None,
                current_value: Option[DataValue],
                max_value: Option[String] = None,
                min_value: Option[String] = None,
                datapoints: Option[Seq[DataValue]] = None,
                unit: Option[UnitType] = None,
                id: Option[BigInt]) extends EemlObject {

  def this(tags: Seq[String], current_value: DataValue,
           max_value: String, min_value: String, datapoints: Seq[DataValue],
           unit: UnitType, id: BigInt) = this(Option(tags), Option(current_value), Option(max_value),
    Option(min_value), Option(datapoints), Option(unit), Option(id))
}

case class Environment(title: Option[String] = None,
                       feed: Option[String] = None,
                       status: Option[String] = None,
                       privateValue: Option[Boolean] = None,
                       description: Option[String] = None,
                       icon: Option[String] = None,
                       website: Option[String] = None,
                       email: Option[String] = None,
                       tags: Option[Seq[String]] = None,
                       location: Option[Location] = None,
                       datastreams: Option[Seq[Data]] = None,
                       updated: Option[XMLGregorianCalendar] = None,
                       creator: Option[String] = None,
                       id: Option[BigInt] = None) extends EemlObject {

  def this(title: String, feed: String, status: String, privateValue: Boolean, description: String,
           icon: String, website: String, email: String, tags: Seq[String],
           location: Location, datastreams: Seq[Data], updated: XMLGregorianCalendar,
           creator: String, id: BigInt) = this(Option(title), Option(feed), Option(status),
    Option(privateValue), Option(description), Option(icon), Option(website), Option(email),
    Option(tags), Option(location), Option(datastreams), Option(updated), Option(creator), Option(id))
}

case class Eeml(environments: Option[Seq[Environment]] = None, version: Option[String] = Some("0.5.1")) extends EemlObject {

  def this(environment: Environment) = this(Option(Seq(environment)))

  def this(environment: Environment, version: String) = this(Option(Seq(environment)), Option(version))

  def this(environments: Seq[Environment], version: String) = this(Option(environments), Option(version))

}
