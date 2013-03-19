package com.example

import akka.actor.{Props, ActorRef, Actor}
import spray.routing._
import spray.http.{DateTime, MediaTypes}
import MediaTypes._
import com.router.HtmlRequestHandler

class MyRouter(pagesRepo: ActorRef, moduleRepo: ActorRef, htmlHandler: ActorRef, jsonHandler: ActorRef) extends Actor with HttpServiceActor {
  import spray.httpx.encoding.Gzip

  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  val js = pathPrefix("js" / Rest)  { fileName =>
    get { encodeResponse(Gzip) { getFromResource("js/" + fileName) } }
  }

  val css = pathPrefix("css" / Rest) { fileName =>
    get { encodeResponse(Gzip) { getFromResource("css/" + fileName) } }
  }

  val favicon = path("favicon.ico") { complete("ok") }

  def receive = runRoute {
    favicon ~ js ~ css ~
    pathPrefix("module-api") {
      detachTo(_ => jsonHandler) { complete("ok") }
    } ~
    detachTo(_ => htmlHandler) { complete("ok") }
  }
}