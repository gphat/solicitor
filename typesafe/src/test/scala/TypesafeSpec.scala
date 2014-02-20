package test

import org.specs2.mutable.Specification
import solicitor.Client
import solicitor.backend.Typesafe

class TypesafeSpec extends Specification {


  "Typesafe backend" should {

    "load files and work" in {
      val client = new Client(
        // Note that the URL doesn't matter here. Typesafe Config
        // picks up application.conf from resources, as it hunts
        // for that via Class.getResource
        backend = new Typesafe()
      )
      client.getDouble("foo") must beSome
      client.getDouble("foo").get must beEqualTo(42D)
    }
  }
}