package solicitor.backend

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import solicitor.Backend

class Static(values: Map[String,String]) extends Backend {

  def getValue(name: String): Future[Option[String]] = future { values.get(name) }
}