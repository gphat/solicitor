package test

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import solicitor.backend.Zk
import solicitor.Client

class ZkSpec extends Specification {

  sequential

  "Zk Backend" should {

    "handle zk connect string" in {
      val zk = new Zk(
        hosts = Seq(
          ("localhost", 8500),
          ("localhost2", 8501)
        ),
        path = "/poop"
      )
      zk.getZkConnectionString must beEqualTo("localhost:8500,localhost2:8501/poop")
    }
  }

  "handle get of string" in new realZk {
    client.getString("foo") must beNone
    Thread.sleep(5000)
    client.getString("foo") must beSome("bar")
    client.getString("foo") must beSome("bar") // again!
  }
}

trait realZk extends Scope {
  val client = new Client(backend = new Zk(
    hosts = Seq(
      ("localhost", 2181)
    )
  ))
}
