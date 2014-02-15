package solicitor

import scala.concurrent.duration._
import scala.concurrent.{Await,Future}
import scala.util.Try

class Client(backend: Backend, timeout: Duration = Duration(1, SECONDS)) {

  def isEnabled(name: String, default: Boolean = false): Boolean = {
    Try(Await.result(backend.isEnabled(name), timeout)).getOrElse(default)
  }

  def isDisabled(name: String): Boolean = !isEnabled(name)

  def getValue(name: String, default: Option[String] = None): Option[String] = {
    Try(Await.result(backend.getValue(name), timeout)).getOrElse(default)
  }

  def shutdown {
    backend.shutdown
  }
}