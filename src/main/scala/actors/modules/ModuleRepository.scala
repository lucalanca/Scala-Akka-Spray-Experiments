package actors.modules

import akka.actor.{ActorRef, Props, Actor}
import akka.pattern._
import common.messages.{ModuleRequest, HTMLPage}
import akka.routing.RoundRobinRouter
import utils.TimeKeeper
import akka.util.Timeout
import concurrent.ExecutionContext
import ExecutionContext.Implicits.global


class ModuleRepository extends Actor {
  implicit val timeout = Timeout(5000)

  // TODO: load modules from a file or something
  var modules : List[ActorRef] = List(
    context.actorOf(Props[Header].withRouter(RoundRobinRouter(nrOfInstances = 100))),
    context.actorOf(Props[Sidebar].withRouter(RoundRobinRouter(nrOfInstances = 100)))
  )

  def receive = {
    // TODO: add some type of versioning here
    case ModuleRequest("header")  => (modules(0) ? "yabadabadoo").mapTo[HTMLPage] pipeTo sender
    case ModuleRequest("sidebar") => (modules(1) ? "yabadabadoo").mapTo[HTMLPage] pipeTo sender
  }
}

trait ModuleActor extends Actor {
  def getHtml() : HTMLPage
  def receive = {
    case _ => sender ! getHtml()
  }
}

class Header extends ModuleActor {
  def getHtml() : HTMLPage = HTMLPage(html.header.render("Bob", 42))
}

class Sidebar extends ModuleActor {
  def getHtml() : HTMLPage = HTMLPage(html.sidebar.render("Bob", 42))
}