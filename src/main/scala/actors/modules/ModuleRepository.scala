package actors.modules

import akka.actor.{ActorRef, Props, Actor}
import akka.pattern._
import common.Messages._
import akka.util.Timeout
import concurrent.ExecutionContext
import twirl.api.Html
import scala.collection.mutable.LinkedHashMap
import common.Messages.ModuleHTMLRequest
import common.Messages.RenderedModule
import scala.Some
import spray.routing.HttpServiceActor
import collection.mutable


class ModuleRepository extends Actor with HttpServiceActor {
  val TAG = "[ModuleRepository] "
  def l(s: String) : Unit = { println(TAG+s) }


  var htmlCache : mutable.HashMap[ModuleHTMLRequest, ModuleHTML] = new mutable.HashMap[ModuleHTMLRequest, ModuleHTML]()
  var jsonCache : mutable.HashMap[ModuleJsonRequest, ModuleJson] = new mutable.HashMap[ModuleJsonRequest, ModuleJson]()
  import context.dispatcher

  implicit val timeout = Timeout(5000)

  override def preStart() = {
    initializeModules()


  }

  // TODO: load modules from a file or something
  var modulesRefMap = LinkedHashMap[String,ActorRef]()

  def initializeModules() = {
    modulesRefMap.put("header"       , context.actorOf(Props[Header]       , "header_actor"))
    modulesRefMap.put("sidebar"      , context.actorOf(Props[Sidebar]      , "sidebar_actor"))
    modulesRefMap.put("infobar"      , context.actorOf(Props[Infobar]      , "infobar_actor"))
    modulesRefMap.put("footer"       , context.actorOf(Props[Footer]       , "footer_actor"))
    modulesRefMap.put("maincontainer", context.actorOf(Props[MainContainer], "maincontainer_actor"))
  }

  def receive = {
    case ModuleHTMLRequest(name,_) => {
      l("got request for " + name)
      (modulesRefMap.get(name)) match {
        case None         => l("couldn't find module " + name)
        case Some(result) => (result ? name).mapTo[RenderedModule] pipeTo sender
      }
    }
    case ModuleJsonRequest(name, path, params, _) => {
      (modulesRefMap.get(name)) match {
        case None         => l("couldn't find module " + name)
        case Some(result) => {
          l("json request")
          (result ? ModuleJsonRequest(name, path, params, 10)).mapTo[String] pipeTo sender
        }
      }
    }
    case AddModule()    => l("TBD")
    case ChangeModule() => l("TBD")
  }
}
