package test

import scala.concurrent.Await
import scala.concurrent.duration._
import solicitor.Client
import solicitor.backend.Static
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class StaticSpec extends Specification {

  sequential

  "Static Backend" should {

    "handle present" in new staticMap {

      val res = client.getValue("foo")
      res must beSome
      res.get must contain("bar")
    }

    "handle absent" in new httpNope {

      client.getValue("getXXX") must beNone
    }
  }
}

trait staticMap extends Scope {
  val client = new Client(backend = new Static(Map("foo" -> "bar")))
}
