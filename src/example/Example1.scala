package example


import scala.xml.PrettyPrinter
import com.kodekutters.eeml.{EemlReader, EemlWriter}
import java.io.{PrintWriter}
import javax.xml.datatype.{DatatypeFactory, XMLGregorianCalendar}

object Example1 {
  def main(args: Array[String]) {

  println("....Example1 start...\n")

  // read a xml file into a Eeml root object
  val eeml = new EemlReader().getFromFile("./xml-files/test1.xml")

  // get the string representation and print it
  val eemlString = EemlWriter.getXmlString(eeml)
  println("eemlString = " + eemlString + "\n")

  // convert the eemlString back to a eeml root object
  val eeml2 = EemlReader.getFromString(eemlString)

  // write the eeml2 object to a PrintWriter (console), but could be a file
  new EemlWriter(Some(new PrintWriter(System.out))).write(eeml2, new PrettyPrinter(80, 3))

  println("\n....Example1 done...")
}
}

