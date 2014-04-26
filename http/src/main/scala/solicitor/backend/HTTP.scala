package solicitor.backend

import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import grizzled.slf4j.Logging
import java.util.concurrent.TimeUnit
import scala.concurrent._
import scala.util.Random
import solicitor.Backend
import spray.can.Http
import spray.client.pipelining._
import spray.http._
import spray.http.HttpMethods._

/**
 * Provides an HTTP backend for fetching information from a remote URL. All
 * responses are assumed to be plain text and any status code other than 200
 * is considered a failure and returns None.
 * {{{
 * val client = new Client(backend = HTTP(hosts = Seq("www.example.com", 80)))
 * // Fetches http://www.example.com/foo/bar
 * val fooBar = client.getValue("foo/bar") // String
 * }}}
 *
 * This backend uses the Spray Host-level API and randomly chooses one
 * of the supplied hosts for each GET request.
 */
class HTTP(hosts: Seq[(String, Int)]) extends Backend with Logging {

  import akka.actor.ActorSystem
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = Timeout(10, TimeUnit.SECONDS) // XXX

  // Iterate through the passed in hosts & ports and ask spray to create
  // a SendReceive function for each host requested.
  val pipes: Seq[Future[SendReceive]] = hosts.map({ h: (String, Int) =>
    for(
      Http.HostConnectorInfo(connector, _) <-
        (IO(Http) ? Http.HostConnectorSetup(host = h._1, port = h._2))
    ) yield sendReceive(connector)
  })

  // Get a count of pipes we have for later.
  val pipeCount = pipes.size

  /**
   * Get a value via HTTP. The name is appended to the baseURL
   * provided to the constructor.
   *
   * @param name The name to fetch.
   */
  override def getString(name: String): Future[Option[String]] = {

    val request = Get("/" + name)
    // Randomly select a pipelined host from the initial list we were
    // given and use it.
    pipes(Random.nextInt(pipeCount)).flatMap(_(request)).map({ response =>
      response.status.intValue match {
        case 200 => Some(response.entity.asString)
        case _ => {
          warn("Bad HTTP Code: " + response.status.intValue)
          warn("Reason: " + response.entity.asString)
          None
        }
      }
    })
  }

  override def shutdown = Http.CloseAll
}
