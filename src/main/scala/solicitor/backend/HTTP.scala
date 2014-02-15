package solicitor.backend

import com.ning.http.client.Response
import dispatch.{Http,Req,url}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import solicitor.Backend

class HTTP(baseUrl: String) extends Backend {

  override def getValue(name: String): String = {
    val req = url(baseUrl) / name
    ""
  }

  def doRequest(req: Req): Future[Response] = {
    Http(req)
  }
}