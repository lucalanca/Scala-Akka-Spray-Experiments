package com.router

import common.Messages.{ModuleJsonRequest, PageRequest}
import concurrent.Await
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import spray.routing.HttpServiceActor
import spray.http.MediaTypes._
import common.Messages.ModuleJsonRequest
import spray.http.HttpResponse

class JsonRequestHandler(modulesRepo: ActorRef) extends Actor with HttpServiceActor {
  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  implicit val timeout = Timeout(5000)

  def receive = runRoute {
    respondWithMediaType(`application/json`) {
      path(PathElement / PathElement) { (module_id, path) =>
        l("json path: %s".format(path))
        complete(jsonFor(module_id, path))
      }
    }
  }

  def jsonFor(moduleId: String, path: String) : String = {
    l("asking for "+path)
    var future = modulesRepo ? ModuleJsonRequest(moduleId, path, "?", 10)
    l("i'm here baby")
    Await.result(future, timeout.duration).asInstanceOf[HttpResponse].toString()
  }
}
