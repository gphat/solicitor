package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import solicitor.backend.Static
import solicitor.{Backend,Client}

class ClientSpec extends Specification {

  sequential

  "Client" should {

    "handle missing values" in new badBackend {
      client.getValue("three") must beNone
    }

    "handle defaults" in new badBackend {
      client.getValueAsBoolean("three", Some(true)).get must beEqualTo(true)
      client.getValueAsBoolean("three", Some(false)).get must beEqualTo(false)

      client.getValueAsDouble("three", Some(1D)).get must beEqualTo(1D)

      client.getValue("foo", Some("bar")).get must beEqualTo("bar")
    }
  }
}

class FailingBackend extends Backend {
  def getValue(name: String): Future[Option[String]] = future {
    throw new RuntimeException("BLAH BLAAH")
    // return None
  }
}

trait badBackend extends Scope {
  val client = new Client(backend = new FailingBackend())
}
