package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.concurrent.Await
import scala.concurrent.duration._
import solicitor.backend.Static
import solicitor.Client

class ConversionSpec extends Specification {

  sequential

  "Conversions" should {

    "handle string" in new exampleMap {

      val res = client.getValue("string")
      res must beSome
      res.get must contain("asdasdasd")
    }

    "handle double" in new exampleMap {

      val res = client.getValueAsDouble("double")
      res must beSome
      res.get must beEqualTo(0.34D)
    }

    "handle booleans" in new exampleMap {
      client.getValueAsBoolean("one").get must beEqualTo(false)
      client.getValueAsBoolean("zero").get must beEqualTo(false)
      client.getValueAsBoolean("booleanTrue").get must beEqualTo(true)
      client.getValueAsBoolean("booleanFalse").get must beEqualTo(false)
      client.getValueAsBoolean("booleanTRue").get must beEqualTo(true)
      client.getValueAsBoolean("booleanFAlse").get must beEqualTo(false)
    }
  }
}

trait exampleMap extends Scope {
  val client = new Client(backend = new Static(Map(
    "one" -> "1",
    "zero" -> "0",
    "longzero" -> "000000",
    "double" -> "0.34",
    "string" -> "asdasdasd",
    "booleanTrue" -> "true",
    "booleanFalse" -> "false",
    "booleanTRue" -> "TRue",
    "booleanFAlse" -> "FAlse"
  )))
}
