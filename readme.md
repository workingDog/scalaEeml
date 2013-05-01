# scalaEeml Overview

scalaEeml is a Scala [EEML](http://www.eeml.org) library, to interact
for example, with [COSM](https://cosm.com/) for the Internet Of Things.

From [EEML](http://www.eeml.org): "Extended Environments Markup Language (EEML)
 ...is a protocol for sharing sensor data between remote responsive environments,
 both physical and virtual. It can be used to facilitate direct connections
 between any two environments; it can also be used to facilitate many-to-many
 connections as implemented by the web service Pachube, which enables people
 to tag and share real time sensor data from objects, devices and spaces around the world."

scalaEeml is based on the XML schema for EEML 0.5.1. This library provides scala objects for the API
data format and the associated xml input/output.

The overall principle was to have a clean set of scala case classes and a separation of concerns for the IO.


# Example

    // read a xml file into a Eeml root object
    val eeml = new EemlReader().getEemlFromFile("./xml-files/test1.xml")

    // get the string representation and print it
    val eemlString = EemlWriter.getXmlString(eeml)
    println("eemlString = " + eemlString + "\n")

    // convert the eemlString back to a eeml root object
    val eeml2 = new EemlReader().getEemlFromString(eemlString)

    // write the eeml2 object to a PrintWriter (console)
    new EemlWriter(Some(new PrintWriter(System.out))).write(eeml2, new PrettyPrinter(80, 3))

# Dependencies

No dependencies yet, but once I introduce JSON as well, it will include some dependencies.

# Notes

All member fields in the case classes are Option[_]. I find this to give more stability with only a small burden
of having to use Some(_) or Option(_). As a relief from this verbosity, I included an overridden set of constructors
that have plain arguments for all classes. The use of Option[_] is a departure from the EEML specifications.

Since I separated the IO from the classes there is no *eemlObj.toXml* to get the string xml representation
of the object. Instead use *EemlWriter.getXmlString(eemlObj)*.
Similarly use *EemlReader.getFromString(eemlString)* to get the object representation of the xml string.

# Status

Under construction, scalaEeml has not been tested yet.

