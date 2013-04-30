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

package com.kodekutters.eeml

import java.io._
import xml.{NodeSeq, dtd, XML, PrettyPrinter}
import scala.Some

/**
 * @author Ringo Wathelet
 *         Date: 29/04/2013
 *         Version: 1
 */

/**
 * represents the extraction of an xml node sequence from a eeml root element
 */
trait XmlExtractor {
  def getXmlFrom[A: EemlToXml](kml: A): NodeSeq
}

/**
 * writes the eeml element object to xml representation
 *
 * @param writer the Writer to use, default a StringWriter
 * @param encoding the encoding, default UTF-8
 * @param xmlDecl if true, write xml declaration, default true
 * @param doctype if not null, write doctype declaration, default null
 */
class EemlWriter(writer: Option[Writer] = Some(new StringWriter()),
                 xmlExtractor: Option[XmlExtractor] = Some(EemlToXml),
                 encoding: String = "UTF-8",
                 xmlDecl: Boolean = true,
                 doctype: dtd.DocType = null) {

  def this(fileName: Option[String]) = this(Some(if (fileName.isDefined) new PrintWriter(new File(fileName.get)) else new PrintWriter(System.out)))

  def this(fileName: String) = this(Option(fileName))

  /**
   * writes the eeml element to xml
   *
   * @param value the eeml element option
   * @param pretty the pretty printer to use, default null
   */
  def write[A: EemlToXml](value: A, pretty: PrettyPrinter = null) = {
    if (writer.isDefined) {
      xmlExtractor match {
        case Some(extractor) => {
          if (pretty == null)
            extractor.getXmlFrom(value).foreach(x => XML.write(writer.get, x, encoding, xmlDecl, doctype))
          else
            extractor.getXmlFrom(value).foreach(x => XML.write(writer.get, XML.loadString(pretty.format(x)), encoding, xmlDecl, doctype))

          writer.get.flush()
        }
        case None => Unit
      }
    }
  }

  /**
   * convenience method to return the string xml representation of the input object
   * @param value the option object
   * @tparam A
   * @return the string xml representation of the input object
   */
  def getXmlString[A: EemlToXml](value: A): String = {
    if (writer.isDefined && writer.get.isInstanceOf[StringWriter]) {
      write(value, new PrettyPrinter(80, 3))
      close()
      writer.get.toString
    } else ""
  }

  def close() = if (writer.isDefined) writer.get.close()

}

object EemlWriter {
  /**
   * convenience method to return the string xml representation of the input object
   * @param value the option object
   * @tparam A
   * @return the string xml representation of the input object
   */
  def getXmlString[A: EemlToXml](value: A): String = new EemlWriter().getXmlString(value)

}


