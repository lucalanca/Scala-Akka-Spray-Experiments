package com.example

import akka.actor.{ActorRef, Actor}
import spray.routing._
import spray.http.MediaTypes
import MediaTypes._
import com.router.RequestHandler


class MyRouter(pagesRepo: ActorRef, handler: RequestHandler) extends Actor with HttpServiceActor {
  import spray.httpx.encoding.Gzip

  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  val js = pathPrefix("js" / Rest) { fileName =>
    get {
      encodeResponse(Gzip) { getFromResource("js/" + fileName) }
    }
  }

  val css = pathPrefix("css" / Rest) { fileName =>
    get {
      encodeResponse(Gzip) { getFromResource("css/" + fileName) }
    }
  }

  def receive = runRoute {
    path("") {
      respondWithMediaType(`text/html`) {complete(handler.viewFor("index")) }
    } ~
    path(PathElement) { pagePath =>
      respondWithMediaType(`text/html`) {complete(handler.viewFor(pagePath)) }
    } ~ js ~ css
  }
}