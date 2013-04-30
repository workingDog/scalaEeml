package com.kodekutters.eeml

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

import scala.xml._
import scala.reflect.runtime.universe._
import scala.Some
import javax.xml.datatype.{DatatypeFactory, XMLGregorianCalendar}

/** Factory for creating eeml objects instances from scala xml NodeSeq */
object EemlFromXml extends EemlExtractor {

  /**
   * creates a eeml root element from the XML NodeSeq, e.g. <eeml> ... </eeml>
   * @param nodeSeq the xml NodeSeq
   * @return a eeml Option
   */
  def makeEeml(nodeSeq: NodeSeq): Option[Eeml] = {
    if (nodeSeq.isEmpty) None
    else
      (nodeSeq \\ "eeml") match {
        case x if (x.isEmpty) => None
        case x => Some(new Eeml(makeEnvironmentSeq(x), makeVersion(x)))
      }
  }

  def makeVersion(nodeSeq: NodeSeq): Option[String] =
    if (nodeSeq.isEmpty) None else getFromNode[String](nodeSeq \ "@version")

  def makeEnvironmentSeq(nodeSeq: NodeSeq): Seq[Environment] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeEnvironment(x \ "environment") } flatten)
  }

  def makeEnvironment(nodeSeq: NodeSeq): Option[Environment] = {
    if (nodeSeq.isEmpty) None else Some(new Environment(
      id = getFromNode[BigInt](nodeSeq \ "@id"),
      updated = makeXMLGregorianCalendar(nodeSeq \ "@updated"),
      creator = getFromNode[String](nodeSeq \ "@creator"),
      title = getFromNode[String](nodeSeq \ "title"),
      feed = getFromNode[String](nodeSeq \ "feed"),
      status = getFromNode[String](nodeSeq \ "status"),
      privateValue = getFromNode[Boolean](nodeSeq \ "private"),
      description = getFromNode[String](nodeSeq \ "description"),
      icon = getFromNode[String](nodeSeq \ "icon"),
      website = getFromNode[String](nodeSeq \ "website"),
      email = getFromNode[String](nodeSeq \ "email"),
      tag = makeTagSeq(nodeSeq \ "tag"),
      location = makeLocation(nodeSeq \ "location"),
      data = makeDataSeq(nodeSeq \ "data")))
  }

  def makeLocation(nodeSeq: NodeSeq): Option[Location] = {
    if (nodeSeq.isEmpty) None else Some(new Location(
      exposure = getFromNode[String](nodeSeq \ "@exposure"),
      domain = getFromNode[String](nodeSeq \ "@domain"),
      disposition = getFromNode[String](nodeSeq \ "@disposition"),
      lat = getFromNode[Double](nodeSeq \ "lat"),
      lon = getFromNode[Double](nodeSeq \ "lon"),
      ele = getFromNode[Double](nodeSeq \ "ele"),
      name = getFromNode[String](nodeSeq \ "name")))
  }

  def makeTagSeq(nodeSeq: NodeSeq): Seq[String] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => getFromNode[String](x) } flatten)
  }

  def makeDataSeq(nodeSeq: NodeSeq): Seq[Data] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeData(x) } flatten)
  }

  def makeData(nodeSeq: NodeSeq): Option[Data] = {
    if (nodeSeq.isEmpty) None else Some(new Data(
      id = getFromNode[BigInt](nodeSeq \ "@id"),
      tag = makeTagSeq(nodeSeq \ "tag"),
      current_value = makeDataValue(nodeSeq \ "current_value"),
      max_value = getFromNode[String](nodeSeq \ "max_value"),
      min_value = getFromNode[String](nodeSeq \ "min_value"),
      datapoints = makeDatapoints(nodeSeq \ "datapoints"),
      unit = makeUnitType(nodeSeq \ "unit")))
  }

  def makeUnitType(nodeSeq: NodeSeq): Option[UnitType] = {
    if (nodeSeq.isEmpty) None else Some(new UnitType(
      symbol = getFromNode[String](nodeSeq \ "@symbol"),
      typeValue = getFromNode[TypeOfUnit](nodeSeq \ "@type"),
      value = getFromNode[String](nodeSeq)))
  }

  def makeDatapoints(nodeSeq: NodeSeq): Seq[DataValue] = {
    if (nodeSeq.isEmpty) Seq.empty else makeDataValueSet(nodeSeq \ "value")
  }

  def makeDataValueSet(nodeSeq: NodeSeq): Seq[DataValue] = {
    if (nodeSeq.isEmpty) Seq.empty else (nodeSeq collect { case x => makeDataValue(x) } flatten)
  }

  def makeDataValue(nodeSeq: NodeSeq): Option[DataValue] = {
    if (nodeSeq.isEmpty) None else Some(new DataValue(
      at = makeXMLGregorianCalendar(nodeSeq \ "@at"),
      value = getFromNode[String](nodeSeq)))
  }

  def makeXMLGregorianCalendar(nodeSeq: NodeSeq): Option[XMLGregorianCalendar] = {
    if (nodeSeq.isEmpty) None else
      Some(DatatypeFactory.newInstance().newXMLGregorianCalendar(getFromNode[String](nodeSeq).getOrElse("")))
  }

  def getFromNode[A: TypeTag](nodeSeq: NodeSeq): Option[A] = {
    if (nodeSeq.isEmpty) None
    else {
      val node = nodeSeq.text.trim
      if (node.isEmpty) None
      else {
        typeOf[A] match {
          case x if x == typeOf[String] => Some(node).asInstanceOf[Option[A]]
          case x if x == typeOf[Double] => try {
            Some(node.toDouble).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          case x if x == typeOf[Int] => try {
            Some(node.toInt).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          case x if x == typeOf[BigInt] => try {
            Some(BigInt(node)).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          case x if x == typeOf[TypeOfUnit] => try {
            Some(node).asInstanceOf[Option[A]]
          } catch {
            case _: Throwable => None
          }
          case x if x == typeOf[Boolean] => node.toLowerCase match {
            case "true" => Some(true).asInstanceOf[Option[A]]
            case "false" => Some(false).asInstanceOf[Option[A]]
            case _ => None
          }
          case _ => None
        }
      }
    }
  }
}
