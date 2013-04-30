package example

/**
 * Author: Ringo Wathelet
 * Date: 30/04/13 
 * Version: 1
 */

import com.kodekutters.eeml.{Environment, Eeml, EemlReader, EemlWriter}
import java.io.{PrintWriter}

object Example2 {
  def main(args: Array[String]) {

    println("....Example2 start...\n")

    // construct a Eeml root object
    val eeml = new Eeml(new Environment(title = Some("testing")))

    // get the string representation and print it
    val eemlString = EemlWriter.getXmlString(Option(eeml))
    println("eemlString = " + eemlString + "\n")

    // convert the eemlString back to a eeml root object
    val eeml2 = EemlReader.getFromString(eemlString)
    println("eeml2 = " + EemlWriter.getXmlString(eeml2) + "\n")

    println("\n....Example2 done...")

  }
}