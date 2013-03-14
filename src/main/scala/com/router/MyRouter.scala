package com.example

import akka.actor.{ActorRef, Props, ActorSystem, Actor}

import spray.routing._
import spray.http.MediaTypes
import MediaTypes._
import testing.RequestHandler
import spray.routing.directives.{DirectoryListing, LogEntry}


class MyRouter(pagesRepo: ActorRef, handler: RequestHandler) extends Actor with HttpServiceActor {
  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  var appPath =  path("") | path("exchange")

  def receive = runRoute {
    path("index") {
      var page_path = "index"
      l("client request for " + page_path)
      l("myserviceactor path " + self.path.toString)
      respondWithMediaType(`text/html`) {complete(handler.viewFor(page_path)) }
    } ~
    path("exchange") {
      var page_path = "exchange"
      l("client request for " + page_path)
      l("myserviceactor path " + self.path.toString)
      respondWithMediaType(`text/html`) {complete(handler.viewFor(page_path)) }
    } ~
    path("footer.js") {
      l("JS: footer" )
      getFromResourceDirectory("footer.js")


    }
  }
}