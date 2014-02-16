package solicitor.backend

import grizzled.slf4j.Logging
import com.ning.http.client.Response
import dispatch.{Http,Req,url}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import solicitor.Backend

class HTTP(baseUrl: String) extends Backend with Logging {

  override def getValue(name: String): Future[Option[String]] = {
    val req = url(baseUrl) / name
    debug("Fetching " + req.url.toString)
    doRequest(req)
  }

  def doRequest(req: Req): Future[Option[String]] = {
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