package test

import scala.concurrent.Await
import scala.concurrent.duration._
import solicitor.Client
import solicitor.backend.Static
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class ConversionSpec extends Specification {

  sequential

  "Conversions" should {

    "handle string" in new exampleMap {

      val res = client.getValue("string")
      res must beSome
      res.get must contain("asdasdasd")
    }
  }
}

trait exampleMap extends Scope {
  val client = new Client(backend = new Static(Map(
    "one" -> "1",
    "zero" -> "0",
    "longzero" -> "000000",
    "decimal" -> "0.34",
    "string" -> "asdasdasd",
    "booleanTrue" -> "true",
    "booleanFalse" -> "false",
    "booleanTRue" -> "TRue",
    "booleanFAlse" -> "FAlse"
  )))
}
