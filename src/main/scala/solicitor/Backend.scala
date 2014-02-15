package solicitor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Backend {

  def getValue(name: String): Future[Option[String]]

  def isEnabled(name: String): Future[Boolean] = {
    getValue(name).map({ maybeValue =>
      maybeValue.map({ v => v match {
        // Handle the two "string boolean" cases
        case s: String if s.equalsIgnoreCase("true") => true
        case s: String if s.equalsIgnoreCase("false") => false
        case _ => false
      } }).getOrElse(false)
    })
  }

  def shutdown: Unit = {
    // Nothing by default.
  }
}