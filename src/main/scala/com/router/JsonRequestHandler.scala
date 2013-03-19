package com.router

import common.Messages.{ModuleJsRequest, PageRequest}
import concurrent.Await
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import spray.routing.HttpServiceActor
import spray.http.MediaTypes._
import common.Messages.ModuleJsRequest

class JsonRequestHandler(modulesRepo: ActorRef) extends Actor with HttpServiceActor {
  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  implicit val timeout = Timeout(5000)

  def receive = runRoute {
    respondWithMediaType(`application/json`) {
      path(PathElement / PathElement) { (module_id, path) =>
        complete(jsonFor(module_id, path))
      }
    }
  }

  def jsonFor(moduleId: String, path: String) : String = {
    var future = modulesRepo ? ModuleJsRequest(moduleId, path)
    Await.result(future, timeout.duration).asInstanceOf[String]
  }
}
