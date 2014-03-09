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
 * XXX val client = new Client(backend = HTTP(baseUrl = "http://www.example.com"))
 * // Fetches http://www.example.com/foo/bar
 * val fooBar = client.getValue("foo/bar") // String
 * }}}
 */
class HTTP(hosts: Seq[(String, Int)]) extends Backend with Logging {

  import akka.actor.ActorSystem
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  implicit val timeout = Timeout(10, TimeUnit.SECONDS) // XXX

  // Iterate through the passed in hosts & ports and ask spray to create
  // a host connector.
  val pipes = hosts.map({ h: (String, Int) =>
    for(
      Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup(host = h._1, port = h._2)
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
    pipes(Random.nextInt(pipeCount)).flatMap(_(request)).map({ r => Some(r.entity.asString) })
    // val response = (IO(Http) ? HttpRequest(GET, "/" + name)).mapTo[HttpResponse]
    // response.map({ r: HttpRequest => Some(r.entity.asString) })
  }

  override def shutdown = Http.CloseAll
}