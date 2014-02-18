package solicitior.backend

import com.typesafe.config.ConfigFactory
import java.net.URL
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import solicitor.Backend

// XXX A reload time, perhaps?
class Typesafe(url: String) extends Backend {

  val config = ConfigFactory.parseURL(new URL(url))

  override def getString(name: String): Future[Option[String]] = {
    future {
      Try(Some(config.getString(name))).getOrElse(None)
    }
  }

  override def getBoolean(name: String): Future[Option[Boolean]] = {
    future {
      Try(Some(config.getBoolean(name))).getOrElse(None)
    }
  }

  override def getDouble(name: String): Future[Option[Double]] = {
    future {
      Try(Some(config.getDouble(name))).getOrElse(None)
    }
  }
}