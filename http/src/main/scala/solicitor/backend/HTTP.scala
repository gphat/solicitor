package solicitor.backend

import grizzled.slf4j.Logging
import com.ning.http.client.Response
import dispatch.{Http,Req,url}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import solicitor.Backend

/**
 * Provides an HTTP backend for fetching information from a remote URL. All
 * responses are assumed to be plain text and any status code other than 200
 * is considered a failure and returns None.
 * {{{
 * val client = new Client(backend = HTTP(baseUurl = "http://www.example.com"))
 * // Fetches http://www.example.com/foo/bar
 * val fooBar = client.getValue("foo/bar") // String
 * }}}
 */
class HTTP(baseUrl: String) extends Backend with Logging {

  /**
   * Get a value via HTTP. The name is appended to the baseURL
   * provided to the constructor.
   *
   * @param The name to fetch.
   */
  override def getString(name: String): Future[Option[String]] = {
    val req = url(baseUrl) / name
    debug("Fetching " + req.url.toString)
    doRequest(req)
  }

  // Private HTTP wrapper
  private def doRequest(req: Req): Future[Option[String]] = {
    Http(req).map({ res =>
      res.getStatusCode match {
        case 200 => Some(res.getResponseBody)
        case _ => {
          // Anything aside from a 200 we'll consider a failure. Log it
          warn("Bad HTTP Code: " + res.getStatusCode)
          warn("Reason: " + res.getResponseBody)
          None
        }
      }
    })
  }

  override def shutdown = Http.shutdown
}