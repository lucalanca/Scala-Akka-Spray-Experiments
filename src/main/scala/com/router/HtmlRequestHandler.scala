package com.router

import common.Messages.{ModuleJsRequest, PageRequest}
import concurrent.Await
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import twirl.api.Html
import spray.routing.HttpServiceActor
import spray.http.MediaTypes._
import common.Messages.ModuleJsRequest
import common.Messages.PageRequest
import spray.http.DateTime

class HtmlRequestHandler(pagesRepo: ActorRef, modulesRepo: ActorRef) extends Actor with HttpServiceActor {
  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  implicit val timeout = Timeout(5000)

  def receive = runRoute {
    path("") {
      respondWithMediaType(`text/html`) {complete(viewFor("index")) }
    } ~
    path("detach") {
      respondWithMediaType(`text/html`) {
        // we detach in order to move the blocking code inside the simpleStringStream off the service actor
        detachTo(singleRequestServiceActor) {
          complete(simpleStringStream)
        }
      }
    } ~
    path(PathElement) { _ =>
      respondWithMediaType(`text/html`) {complete(viewFor("exchange")) }
    }
  }

  def viewFor(path: String) : String = {
    var future = pagesRepo ? PageRequest(path)
    Await.result(future, timeout.duration).asInstanceOf[Html].toString
  }


  lazy val streamStart = " " * 2048 + "<html><body><h2>A streaming response</h2><p>(for 15 seconds)<ul>"
  lazy val streamEnd = "</ul><p>Finished.</p></body></html>"

  def simpleStringStream: Stream[String] = {
    val secondStream = Stream.continually {
      // CAUTION: we block here to delay the stream generation for you to be able to follow it in your browser,
      // this is only done for the purpose of this demo, blocking in actor code should otherwise be avoided
      Thread.sleep(500)
      "<li>" + DateTime.now.toIsoDateTimeString + "</li>"
    }
    streamStart #:: secondStream.take(15) #::: streamEnd #:: Stream.empty
  }
}
