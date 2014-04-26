package solicitor.backend

import org.apache.commons.codec.binary.Base64
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import spray.json.DefaultJsonProtocol._

/**
 * Provides a backend for fetching information from [[Consul's http://www.consul.io/]]
 * KV HTTP API. All values are assumed to be UTF-8 strings and any status code
 * other than 200 is considered a failure and returns None.
 * {{{
 * val client = new Client(backend = HTTP(hosts = Consul("www.example.com", 8500)))
 * // Fetches http://www.example.com/v1/kv/foo/bar
 * val fooBar = client.getValue("foo/bar") // String
 * }}}
 *
 * This backend uses the Solicitior HTTP backend and follows the same behaviors.
 */
class Consul(hosts: Seq[(String, Int)]) extends HTTP(hosts) {

  case class V1ConsulBody(
    CreateIndex: Long,
    ModifyIndex: Long,
    Key: String,
    Flags: Long,
    Value: String
  )

  object ConsulJsonProtocol extends DefaultJsonProtocol {
    implicit val v1ConsulFormat = jsonFormat5(V1ConsulBody)
  }

  import ConsulJsonProtocol._

  val v1kvPath = "v1/kv/"

  override def getString(name: String): Future[Option[String]] = {
    debug("Fetching path from consul: " + v1kvPath + name)
    super.getString(v1kvPath + name) map { maybeBody =>
      maybeBody map { body =>
        // Consul returns a list of one element. Take the first one!
        val consulValue = body.parseJson.convertTo[Seq[V1ConsulBody]]
        consulValue.headOption map { item =>
          // Consul Base64 encodes its strings. We'll assume it's UTF-8
          new String(Base64.decodeBase64(item.Value), "UTF-8")
        }
      } getOrElse(None)
    }
  }
}
