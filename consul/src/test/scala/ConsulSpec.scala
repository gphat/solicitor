package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import solicitor.backend.Consul
import solicitor.Client

class ConsulSpec extends Specification {

  sequential

  "Consul Backend" should {

    "handle 200" in new httpBin {

      val res = client.getString("poop")
      res must beSome
      res.get must contain("butt")
    }

    "handle 404" in new httpBin {

      client.getString("getXXX") must beNone
    }
  }
}

trait httpBin extends Scope {
  val client = new Client(backend = new Consul(
    hosts = Seq(("localhost", 8500))
  ))
}
