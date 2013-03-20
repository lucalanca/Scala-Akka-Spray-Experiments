package com.example

import spray.can.server.SprayCanHttpServerApp
import akka.actor.{Props, ActorRef}
import com.typesafe.config.{ConfigObject, Config, ConfigFactory}
import actors.pages.PagesRepository
import actors.modules.ModuleRepository
import com.router.{JsonRequestHandler, HtmlRequestHandler}
import actors.cougar.{EROClient, CougarClientsRepository}
import com.router.HtmlRequestHandler
import common.Messages.ModuleJsonRequest
import concurrent.Await
import akka.util.Timeout
import akka.pattern.ask


object Boot extends App with SprayCanHttpServerApp {


  var modulesRepo : ActorRef = system.actorOf(Props[ModuleRepository],                  "modules-repository")
  var pagesRepo   : ActorRef = system.actorOf(Props(new PagesRepository(modulesRepo)),  "pages-repository")
  var htmlHandler : ActorRef = system.actorOf(Props(new HtmlRequestHandler(pagesRepo, modulesRepo)), "html-handler")
  var jsonHandler : ActorRef = system.actorOf(Props(new JsonRequestHandler(modulesRepo)), "json-handler")


  val service = system.actorOf(Props(new MyRouter(pagesRepo, modulesRepo, htmlHandler, jsonHandler)), "router")






  newHttpServer(service) ! Bind(interface = "localhost", port = 8080)
}