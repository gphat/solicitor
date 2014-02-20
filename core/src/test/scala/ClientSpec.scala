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
      client.getString("three") must beNone
    }

    "handle defaults" in new badBackend {
      client.getBoolean("three", Some(true)).get must beEqualTo(true)
      client.getBoolean("three", Some(false)).get must beEqualTo(false)

      client.getDouble("three", Some(1D)).get must beEqualTo(1D)

      client.getString("foo", Some("bar")).get must beEqualTo("bar")
    }
  }
}

class FailingBackend extends Backend {
  def getString(name: String): Future[Option[String]] = future {
    throw new RuntimeException("BLAH BLAAH")
    // return None
  }
}

trait badBackend extends Scope {
  val client = new Client(backend = new FailingBackend())
}
