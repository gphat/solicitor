package solicitor.backend

import grizzled.slf4j.Logging
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache.{NodeCache,NodeCacheListener}
import org.apache.curator.retry.BoundedExponentialBackoffRetry
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import solicitor.Backend

/**
 * Provides a backend for fetching information from a path in Zookeeper. Uses
 * a non-blocking backend that is not guaranteed to be accurate or to be
 * in sync transactionally with Zk. It gets there eventually. :)

 * Takes a list of hostname and port pairs and optional path which are converted
 * into a Zookeeper connection string (e.g. host1:2181,host2:2181/solicitor).
 * {{{
 * val client = new Client(backend = new Zk(
 *   hosts = Seq(
 *     ("localhost", 2181)
 *   )
 * ))
 * }}}
 */
class Zk(hosts: Seq[(String, Int)], path: String = "/solicitor") extends Backend with Logging {

  val values = scala.collection.mutable.HashMap.empty[String,NodeCache]

  lazy val zkConnectString = hosts.map({
    case (k,v) => k + ":" + v.toString
  }).toSeq.mkString(",") + path

  lazy val curator = CuratorFrameworkFactory.newClient(
    zkConnectString,
    new BoundedExponentialBackoffRetry(100, 1000, 5)
  );

  // For test purposes
  def getZkConnectionString = zkConnectString

  def getString(name: String): Future[Option[String]] = future {
    if(values.isEmpty) {
      // If we get here then the cache is empty and we should start
      // the client
      curator.start
      curator.getZookeeperClient.blockUntilConnectedOrTimedOut;
    }
    val zkValue = values.get(name) getOrElse {
      // Create a new node cache and return it since we didn't already
      // have this one.
      debug("First time seeing this key, creating NodeCache")
      val nc = new NodeCache(curator, "/" + name) // Append the /
      values += name -> nc
      nc.start
      nc
    }
    val bites = try {
      val current = zkValue.getCurrentData
      // currentData can be null…
      if(current != null) {
        current.getData
      } else {
        null
      }
    } catch {
      case e: Exception => {
        warn("Error fetching value: " + e.getMessage)
        null
      }
    }
    // Check if we got anything back
    if(bites == null) {
      None
    } else {
      Some(new String(bites))
    }
  }

  override def shutdown = {
    values.foreach { case (k, nc) =>
      nc.close
    }
    curator.close
  }
}
