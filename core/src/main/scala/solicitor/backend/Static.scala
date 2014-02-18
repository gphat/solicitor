package solicitor.backend

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import solicitor.Backend

/**
 * Provides a simple Map of keys and values for testing or mocking up
 * Solicitor. Not advised for production use unless you are stubbing
 * out code and need to provide a safe intermediary configuration before
 * choosing a real backend.
 * 
 * Note that this uses String,String because all other backends will likely
 * do string conversion.
 */
class Static(values: Map[String,String]) extends Backend {

  /**
   * Gets a value from the Static map. No type converstion takes place.
   */
  def getString(name: String): Future[Option[String]] = future { values.get(name) }
}