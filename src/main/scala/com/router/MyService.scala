package com.example

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import spray.routing._
import actors.pages.{PagesRepository, IndexActor}
import common.messages._
import concurrent.{Future, Await}
import spray.http.MediaTypes
import MediaTypes._
import twirl.api.Html

class MyServiceActor extends Actor with HttpServiceActor {
  var pagesRepo  = context.actorOf(Props[PagesRepository])
  implicit val timeout = Timeout(5000)

  def receive = runRoute {
    get {
      path(PathElement) { page_path =>
        var future : Future[Html] = (pagesRepo ? PageRequest(page_path)).mapTo[Html]
        println("received rendered: " + future)
        var result = Await.result(future, timeout.duration)
        respondWithMediaType(`text/html`) {complete(result.toString) }
      }
    }
  }
}