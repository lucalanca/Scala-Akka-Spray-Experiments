package com.example

import spray.can.server.SprayCanHttpServerApp
import akka.actor.{ActorRef, Props}
import com.typesafe.config.{ConfigObject, Config, ConfigFactory}
import actors.pages.PagesRepository
import actors.modules.ModuleRepository
import com.router.RequestHandler


object Boot extends App with SprayCanHttpServerApp {


  var modulesRepo : ActorRef = system.actorOf(Props[ModuleRepository],                  "modules-repository")
  var pagesRepo   : ActorRef = system.actorOf(Props(new PagesRepository(modulesRepo)),  "pages-repository")
  var handler     : RequestHandler = new RequestHandler(pagesRepo)

  val service = system.actorOf(Props(new MyRouter(pagesRepo, handler)), "router")




  newHttpServer(service) ! Bind(interface = "localhost", port = 8080)
}