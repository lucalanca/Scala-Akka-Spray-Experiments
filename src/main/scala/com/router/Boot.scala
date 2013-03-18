package com.example

import spray.can.server.SprayCanHttpServerApp
import akka.actor.{ActorRef, Props}
import com.typesafe.config.{ConfigObject, Config, ConfigFactory}
import actors.pages.PagesRepository
import actors.modules.ModuleRepository
import com.router.RequestHandler
import actors.cougar.CougarClientsRepository


object Boot extends App with SprayCanHttpServerApp {


  var modulesRepo : ActorRef = system.actorOf(Props[ModuleRepository],                  "modules-repository")
  var pagesRepo   : ActorRef = system.actorOf(Props(new PagesRepository(modulesRepo)),  "pages-repository")
  var handler     : RequestHandler = new RequestHandler(pagesRepo, modulesRepo)

  val service = system.actorOf(Props(new MyRouter(pagesRepo, modulesRepo, handler)), "router")

  var cougerTest : ActorRef = system.actorOf(Props[CougarClientsRepository], "cougarTest")
  cougerTest ! "Testing"


  newHttpServer(service) ! Bind(interface = "localhost", port = 8080)
}