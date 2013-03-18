package com.router

import common.Messages.{ModuleJsRequest, PageRequest}
import concurrent.Await
import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask
import twirl.api.Html

class RequestHandler(pagesRepo: ActorRef, modulesRepo: ActorRef) {
  val TAG = "[MyRouter] "
  def l(s: String) : Unit = { println(TAG+s) }

  implicit val timeout = Timeout(5000)

  def viewFor(path: String) : String = {
    var future = pagesRepo ? PageRequest(path)
    Await.result(future, timeout.duration).asInstanceOf[Html].toString
  }

  def jsonFor(moduleId: String, path: String) : String = {
    var future = modulesRepo ? ModuleJsRequest(moduleId, path)
    Await.result(future, timeout.duration).asInstanceOf[String]
  }
}
