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

import xml._
import scala.Predef._
import scala.{Option, Some}

/**
 *
 * converts eeml objects to xml node sequences.
 *
 * @author Ringo Wathelet
 * Date: 29/04/13
 * Version: 1
 */

trait EemlToXml[A] {
  def toXml(value: A): NodeSeq
}

trait EemlToXmlSeq[A] {
  def toXml(value: A): Seq[NodeSeq]
}


/** Factory to convert eeml objects to scala xml NodeSeq */
object EemlToXml extends XmlExtractor {

  implicit object LocationToXml extends EemlToXml[Option[Location]] {
    def toXml(locationOption: Option[Location]): NodeSeq = {
      locationOption match {
        case Some(location) => <location exposure={if (location.exposure.isDefined) location.exposure.get else null} disposition={if (location.disposition.isDefined) location.disposition.get else null} domain={if (location.domain.isDefined) location.domain.get else null}>
          {makeXmlNode("name",location.name)}
          {makeXmlNode("lat",location.lat)}
          {makeXmlNode("lon",location.lon)}
          {makeXmlNode("ele",location.ele)}
        </location>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object DataValueToXml extends EemlToXml[Option[DataValue]] {
    def toXml(dataValueOption: Option[DataValue]): NodeSeq = {
      dataValueOption match {
        case Some(dataValue) =>
          <value at={if (dataValue.at.isDefined) dataValue.at.get.toString else null}>
            {dataValue.value.getOrElse("")}
          </value>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object DatapointsToXml extends EemlToXml[Option[Seq[DataValue]]] {
    def toXml(datapointsOption: Option[Seq[DataValue]]): NodeSeq = {
      datapointsOption match {
        case Some(datapoints) =>
          <datapoints>
            {for (x <- datapoints) yield getXmlFrom(Option(x))}
          </datapoints>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object UnitTypeToXml extends EemlToXml[Option[UnitType]] {
    def toXml(unitTypeOption: Option[UnitType]): NodeSeq = {
      unitTypeOption match {
        case Some(unitType) =>
          <unit type={if (unitType.typeValue.isDefined) unitType.typeValue.get.toString else null} symbol={if (unitType.symbol.isDefined) unitType.symbol.get else null}>
            {unitType.value.getOrElse("")}
          </unit>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object DataToXml extends EemlToXml[Option[Data]] {
    def toXml(dataOption: Option[Data]): NodeSeq = {
      dataOption match {
        case Some(data) =>
          <data id={if (data.id.isDefined) data.id.get.toString else null}>
            {for (x <- data.tag) yield <tag>{x}</tag>}
            {current_valueToXml(data.current_value)}
            {makeXmlNode("max_value",data.max_value)}
            {makeXmlNode("min_value",data.min_value)}
            {getXmlFrom(data.unit)}
            {getXmlFrom(Option(data.datapoints))}
          </data>
        case None => NodeSeq.Empty
      }
    }
  }

  implicit object DataSeqToXml extends EemlToXmlSeq[Option[Seq[Data]]] {
    def toXml(dataSet: Option[Seq[Data]]): Seq[NodeSeq] = {
      dataSet match {
        case Some(dSet) => (dSet collect {
          case x => getXmlFrom(Option(x.asInstanceOf[Data]))
        } filter (x => (x != null) && (x != None)) toSeq)
        case None => Seq.empty
      }
    }
  }

  implicit object EnvironmentToXml extends EemlToXml[Option[Environment]] {
    def toXml(envOption: Option[Environment]): NodeSeq = {
      envOption match {
        case Some(env) =>
          <environment id={if (env.id.isDefined) env.id.get.toString else null} updated={if (env.updated.isDefined) env.updated.get.toString else null} creator={if (env.creator.isDefined) env.creator.get else null}>
            {makeXmlNode("title",env.title)}
            {makeXmlNode("feed",env.feed)}
            {makeXmlNode("status",env.status)}
            {makeXmlNode("website",env.website)}
            {makeXmlNode("private",env.privateValue)}
            {makeXmlNode("icon",env.icon)}
            {makeXmlNode("email",env.email)}
            {makeXmlNode("description",env.description)}
            {for (x <- env.tag) yield <tag>{x}</tag>}
            {getXmlFrom(env.location)}
            {getXmlSeqFrom(Option(env.data))}
          </environment>
        case None => NodeSeq.Empty
      }
    }
  }

    implicit object EemlToXml extends EemlToXml[Option[Eeml]] {
      def toXml(eemlOption: Option[Eeml]): NodeSeq = {
        eemlOption match {
          case Some(eeml) =>
            <eeml xmlns="http://www.eeml.org/xsd/0.5.1"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  version={if (eeml.version.isDefined) eeml.version.get else null}
                  xsi:schemaLocation="http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd">
              {for (x <- eeml.environments) yield getXmlFrom(Option(x))}
            </eeml>
          case None => NodeSeq.Empty
        }
      }
    }

  def makeXmlNode(name: String, valueOption: Option[_]): NodeSeq = {
    valueOption match {
      case Some(value) => value match {
        case _ => <a>{value}</a>.copy(label = name)
      }
      case None => NodeSeq.Empty
    }
  }

  def current_valueToXml(current_valueOption: Option[DataValue]): NodeSeq = {
      current_valueOption match {
        case Some(current_value) =>
          <current_value at={if (current_value.at.isDefined) current_value.at.get.toString else null}>
            {current_value.value.getOrElse("")}
          </current_value>
        case None => NodeSeq.Empty
      }
    }

  def getXmlFrom[A: EemlToXml](xal: A) = implicitly[EemlToXml[A]].toXml(xal)

  def getXmlSeqFrom[A: EemlToXmlSeq](kml: A) = implicitly[EemlToXmlSeq[A]].toXml(kml)

}

