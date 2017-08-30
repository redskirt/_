package com.gtan.repox

import akka.actor.ActorRef
import com.gtan.repox.config.Config
import com.gtan.repox.data.Repo
import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.{AsyncHandler, HttpResponseBodyPart, HttpResponseHeaders, HttpResponseStatus}
import com.typesafe.scalalogging.LazyLogging
import io.undertow.util.StatusCodes

class HeadAsyncHandler(val worker: ActorRef,val uri: String, val repo: Repo) extends AsyncHandler[Unit] with LazyLogging {
  var statusCode = 200

  @volatile private var canceled = false

  def cancel(): Unit = {
    canceled = true
  }

  override def onThrowable(t: Throwable): Unit = {
    if(!canceled) {
      logger.debug("HeadAsyncHandler throws ", t)
      worker ! HeadWorker.Failed(t)
    }
  }

  override def onCompleted(): Unit = {
    /* we don't get here actually */
  }

  override def onBodyPartReceived(bodyPart: HttpResponseBodyPart): STATE = {
    // we don't get here actually
    STATE.ABORT
  }

  override def onStatusReceived(responseStatus: HttpResponseStatus): STATE = {
    if (!canceled) {
      statusCode = responseStatus.getStatusCode
      statusCode match {
        case StatusCodes.NOT_FOUND =>
          Repox.head404Cache ! Head404Cache.NotFound(uri, repo)
          for(child <- Config.repos.filter(_.parentId.exists(repo.id.contains))){
            Repox.head404Cache ! Head404Cache.NotFound(uri, child)
          }
        case _ =>
      }
      STATE.CONTINUE
    } else STATE.ABORT
  }

  override def onHeadersReceived(headers: HttpResponseHeaders): STATE = {
    if (!canceled) {
      import scala.collection.JavaConverters._
      worker ! HeadWorker.Responded(statusCode, mapAsScalaMapConverter(headers.getHeaders).asScala.toMap)
    }
    STATE.ABORT
  }

}
