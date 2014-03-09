package solicitor

import scala.concurrent.duration._
import scala.concurrent.{Await,Future}
import scala.util.{Random,Try}

class Client(backend: Backend, timeout: Duration = Duration(1, SECONDS)) {

  val rng = new Random(System.currentTimeMillis)

  /**
   * Returns true if the name supplied returns a true value. Merely a flattened
   * wrapper around getValueAsBoolean.
   * 
   * @param name The name to check
   * @param default A default value in the event of a failure to retrieve.
   */
  def isEnabled(name: String, default: Boolean = false): Boolean =
    getBoolean(name, Some(default)).getOrElse(false)

  /**
   * Returns false if the name supplied returns a true value.
   * 
   * @param name The name to check
   * @param default A default value in the event of a failure to retrieve.
   */
  def isDisabled(name: String): Boolean = !isEnabled(name)

  /**
   * Randomly decides if a name is enabled using a percentage chance. Values
   * should be a number between 0 and 1. In the event that a value cannot
   * be retrieved the default is used.
   *
   * @param name The name to fetch.
   * @param default A default value in the event of a failure to retrieve.
   */
  def decideEnabled(name: String, default: Boolean = false): Boolean = {
    getDouble(name).map({ chance =>
      // Fetch a double from the config.
      if(chance <= rng.nextDouble) {
        false
      } else {
        true
      }
    }).getOrElse(default)
  }

  /**
   * Return a value for the given name.
   * 
   * @param name The name to fetch.
   * @param default A default value in the event of a failure to retrieve.
   */
  def getString(name: String, default: Option[String] = None): Option[String] =
    Try(Await.result(backend.getString(name), timeout)).getOrElse(default)

  /**
   * Return a value, converted to Boolean, for the given name.
   * 
   * @param name The name to fetch
   * @param default A default value in the event of a failure to retrieve.
   */
  def getBoolean(name: String, default: Option[Boolean] = None): Option[Boolean] =
    Try(Await.result(backend.getBoolean(name), timeout)).getOrElse(default)

  /**
   * Return a value, converted to Double, for the given name.
   * 
   * @param name The name to fetch
   * @param default A default value in the event of a failure to retrieve.
   */
  def getDouble(name: String, default: Option[Double] = None): Option[Double] =
    Try(Await.result(backend.getDouble(name), timeout)).getOrElse(default)

  /**
   * Closes any resources allocated by Solicitor and it's backends.
   */
  def shutdown {
    backend.shutdown
  }
}