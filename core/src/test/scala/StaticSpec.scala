package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.concurrent.Await
import scala.concurrent.duration._
import solicitor.backend.Static
import solicitor.Client

class StaticSpec extends Specification {

  sequential

  "Static Backend" should {

    "handle present" in new staticMap {

      val res = client.getString("foo")
      res must beSome
      res.get must contain("bar")
    }

    "handle absent" in new staticMap {

      client.getString("getXXX") must beNone
    }

    "handle boolean" in new staticMap {
      client.isEnabled("yes") must beTrue
      client.isEnabled("no") must beFalse
      client.isDisabled("no") must beTrue
      client.isDisabled("yes") must beFalse
    }

    "handle deciding percentages" in new staticMap {
      client.decideEnabled("gorch") must beTrue
      client.decideEnabled("glom") must beFalse
      client.decideEnabled("glump") must beOneOf(true, false)
    }
  }
}

trait staticMap extends Scope {
  val client = new Client(backend = new Static(
    Map(
      "foo" -> "bar",
      "yes" -> "true",
      "no" -> "false",
      "gorch" -> "1.0",
      "glom" -> "0.0",
      "glup" -> "0.5"
    )
  ))
}
