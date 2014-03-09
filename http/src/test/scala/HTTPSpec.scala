package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import solicitor.backend.HTTP
import solicitor.Client

class HTTPSpec extends Specification {

  sequential

  "HTTP Backend" should {

    "handle 200" in new httpBin {

      val res = client.getString("foo/bar")
      res must beSome
      res.get must contain("123")
    }

    "handle 404" in new httpBin {

      client.getString("getXXX") must beNone
    }

    "handle no answer" in new httpNope {

      client.getString("getXXX") must beNone
    }
  }
}

trait httpBin extends Scope {
  val client = new Client(backend = new HTTP(
    hosts = Seq(("localhost", 8000))
  ))
}

trait httpNope extends Scope {
  val client = new Client(backend = new HTTP(
    hosts = Seq(("thisdomainprobablywonteverexist.com", 80))
  ))
}