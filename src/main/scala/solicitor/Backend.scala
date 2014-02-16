package solicitor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait Backend {

  def getValue(name: String): Future[Option[String]]

  def getValueAsBoolean(name: String): Future[Option[Boolean]] = {
    getValue(name).map({ maybeValue =>
      maybeValue.map({ v =>
        stringToBoolean(v)
      })
    })
  }

  def getValueAsDouble(name: String): Future[Option[Double]] = {
    getValue(name).map({ maybeValue =>
      // Use a flatMap here because our Try returns an Option.
      maybeValue.flatMap({ v =>
        Try({ Some(v.toDouble) }).getOrElse(None)
      })
    })
  }

  def isEnabled(name: String): Future[Boolean] = {
    getValue(name).map({ maybeValue =>
      maybeValue.map({ v =>
        stringToBoolean(v)
      }).getOrElse(false)
    })
  }

  def stringToBoolean(v: String): Boolean = {
    v match {
      // Handle the two "string boolean" cases
      case s: String if v.equalsIgnoreCase("true") => true
      case s: String if v.equalsIgnoreCase("false") => false
      case _ => false
    }
  }

  def shutdown: Unit = {
    // Nothing by default.
  }
}