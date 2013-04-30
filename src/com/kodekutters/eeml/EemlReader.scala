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

import org.xml.sax.InputSource
import scala.xml.XML._
import scala.xml.{NodeSeq, XML}
import java.io.{File, FileDescriptor }
import scala.xml.Source._
import scala.language.postfixOps

/**
 * the extraction/creation of a eeml root element object from an xml node sequence
 * Specifically getting a Eeml root object from <eeml> ... </eeml> nodeSeq
 */
trait EemlExtractor {
  def makeEeml(nodeSeq: NodeSeq): Option[Eeml]
}

/**
 * Reads a eeml root element (Eeml) from various file, string and NodeSeq input sources
 *
 * @param eemlExtractor the EemlExtractor object used to extract eeml from xml, default eemlFromXml
 * @param parser the SAX XML parser, default scala.xml.XML.parser
 * @see eemlFromXml
 */

class EemlReader(eemlExtractor: Option[EemlExtractor] = Some(EemlFromXml),
                    parser: scala.xml.SAXParser = scala.xml.XML.parser) {

  /**
   * get a eeml root element from the input source
   *
   * @param source eeml input source, such as a file, a file name, a file descriptor
   * @return a eeml root element option
   */
  def loadEeml(source: InputSource): Option[Eeml] = {
    Some(loadXML(source, parser)) match {
      case Some(nodeSeq) => getEeml(nodeSeq)
      case _ => None
    }
  }

  /**
   * get a sequence of eeml root element options from the input zip file
   * @param file the input zip file
   * @return a sequence of eeml root element options
   */
  def getFromZipFile(file: File): Seq[Option[Eeml]] = {
    import scala.collection.JavaConversions._
    if (!file.getName.toLowerCase.endsWith(".zip")) Seq.empty
    else {
      (new java.util.zip.ZipFile(file).entries.
        filter(_.getName.toLowerCase.endsWith(".xml")).
        collect { case xmlFile => getFromFile(xmlFile.getName) } toSeq)
    }
  }

  /**
   * get a eeml root element from the input file
   * @param file the input xml file
   * @return a eeml root element option
   */
  def getFromFile(file: File): Option[Eeml] = loadEeml(fromFile(file))

  /**
   * get a eeml root element from the input file descriptor
   * @param fd the input xml file descriptor
   * @return a eeml root element option
   */
  def getFromFile(fd: FileDescriptor): Option[Eeml] = loadEeml(fromFile(fd))

  /**
   * get a eeml root element from the input file name
   * @param name the input file name
   * @return a eeml root element option
   */
  def getFromFile(name: String): Option[Eeml] = loadEeml(fromFile(name))

  /**
   * get a eeml root element from its input string representation
   * @param xmlString the input xml string
   * @return a eeml root element option
   */
  def getFromString(xmlString: String): Option[Eeml] = getEeml(XML.loadString(xmlString))

  /**
   * get a eeml root element from its xml NodeSeq
   * @param nodeSeq the input xml node sequence
   * @return a eeml root element option
   */
  def getFromNodeSeq(nodeSeq: scala.xml.NodeSeq): Option[Eeml] = getEeml(nodeSeq)

  /**
   * creates a eeml root element from the Node Sequence
   * @param nodeSeq the xml node sequence
   * @return a eeml root element option
   */
  private def getEeml(nodeSeq: scala.xml.NodeSeq): Option[Eeml] = {
    eemlExtractor match {
      case Some(extractor) => extractor.makeEeml(nodeSeq)
      case _ => None
    }
  }
}

object EemlReader {
  /**
   * get a eeml root element from its input string representation
   * @param xmlString the input xml string
   * @return a eeml root element option
   */
  def getFromString(xmlString: String): Option[Eeml] = new EemlReader().getFromString(xmlString)

}

