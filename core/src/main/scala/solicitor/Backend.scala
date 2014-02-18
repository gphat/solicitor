package solicitor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
 * Trait for implementing backends for Solicitor. Implementations need
 * only implement the getValue method and return a Future[Option[String]].
 *
 * ==Types==
 * Types in Solicitor are very naive. You may convert a string to a Boolean,
 * in which case a case-insensitive check is done for "true" or "false".
 * 
 * Any number is converted to a double if it can be successfully converted
 * via toDouble. Any failure to convert will be translated as a None.
 * 
 * Otherwise, getValue can return a string and the user of Solicitor can
 * do their own conversion. Double was chosen for easy implementation of
 * percentages and boolean for obvious reasons.
 */
trait Backend {

  /**
   * Get a value from the backend.
   *
   * @param name Name of key
   */
  def getString(name: String): Future[Option[String]]

  /**
   * Get a value and convert the result to boolean. Works only with
   * case-insensitive comparisons to "true" and "false".
   *
   * @param name Name of key
   */
  def getBoolean(name: String): Future[Option[Boolean]] = {
    getString(name).map({ maybeValue =>
      maybeValue.map({ v =>
        stringToBoolean(v)
      })
    })
  }

  /**
   * Get a value and convert the result to double. Works only if
   * String's toDouble works.
   *
   * @param name Name of key
   */
  def getDouble(name: String): Future[Option[Double]] = {
    getString(name).map({ maybeValue =>
      // Use a flatMap here because our Try returns an Option.
      maybeValue.flatMap({ v =>
        Try({ Some(v.toDouble) }).getOrElse(None)
      })
    })
  }

  /**
   * Get a value.
   *
   * @param name Name of key
   */
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