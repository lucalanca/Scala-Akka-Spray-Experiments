package actors.modules

import akka.actor.{ActorRef, Props, Actor}
import akka.pattern._
import common.messages.{ModuleRequest, ModuleHTML}
import akka.routing.RoundRobinRouter
import utils.TimeKeeper
import akka.util.Timeout
import concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import twirl.api.Html


class ModuleRepository extends Actor {
  implicit val timeout = Timeout(5000)

  // TODO: load modules from a file or something
  var modules : List[ActorRef] = List(
    context.actorOf(Props[Header].withRouter(RoundRobinRouter(nrOfInstances = 10))),
    context.actorOf(Props[Sidebar].withRouter(RoundRobinRouter(nrOfInstances = 10))),
    context.actorOf(Props[Infobar].withRouter(RoundRobinRouter(nrOfInstances = 10))),
    context.actorOf(Props[Footer].withRouter(RoundRobinRouter(nrOfInstances = 10))),
    context.actorOf(Props[MainContainer].withRouter(RoundRobinRouter(nrOfInstances = 10)))
  )

  def receive = {
    // TODO: add some type of versioning here
    case ModuleRequest("header")         => (modules(0) ? "yabadabadoo").mapTo[ModuleHTML] pipeTo sender
    case ModuleRequest("sidebar")        => (modules(1) ? "yabadabadoo").mapTo[ModuleHTML] pipeTo sender
    case ModuleRequest("infobar")        => (modules(2) ? "yabadabadoo").mapTo[ModuleHTML] pipeTo sender
    case ModuleRequest("footer")         => (modules(3) ? "yabadabadoo").mapTo[ModuleHTML] pipeTo sender
    case ModuleRequest("maincontainer")  => (modules(4) ? "yabadabadoo").mapTo[ModuleHTML] pipeTo sender
  }
}

trait ModuleActor extends Actor {
  val data : ModuleHTML
  def receive = {
    case _ => sender ! data
  }
}

class Header extends ModuleActor {
  val data : ModuleHTML = ModuleHTML(html.header.render(), html.header_head.render("header.css", "header.js"))
}

class Sidebar extends ModuleActor {
  val data : ModuleHTML = ModuleHTML(html.sidebar.render("Bob", 42), html.header_head.render("sidebar.css", "sidebar.css"))
}

class Infobar extends ModuleActor {
  val data : ModuleHTML = ModuleHTML(html.infobar.render(), html.infobar_head.render("infobar.css", "infobar.css"))
}

class Footer extends ModuleActor {
  val data : ModuleHTML = ModuleHTML(html.footer.render(), html.footer_head.render("footer.css", "footer.js"))
}

class MainContainer extends ModuleActor {
  val data : ModuleHTML = ModuleHTML(html.maincontainer.render(), html.maincontainer_head.render("maincontainer.css", "maincontainer.js"))
}