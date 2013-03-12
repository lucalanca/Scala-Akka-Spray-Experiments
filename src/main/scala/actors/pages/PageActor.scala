package actors.pages

import akka.actor._
import common.messages._
import akka.pattern._
import akka.util.Timeout
import utils.TimeKeeper
import concurrent.{Await, Future, ExecutionContext}
import actors.modules.{ModuleRepository, ModuleActor}
import scala.xml._
import twirl.api.Html
import collection.mutable.{ListBuffer, ArrayBuffer}
import akka.actor.ActorRef


trait PageActor extends Actor {
  implicit val timeout = Timeout(500)
  var modules : List[String]
  var moduleRepository : ActorRef = context.actorOf(Props[ModuleRepository])
  var pageName   : String
  def render(page : PageHTML) : Html
  import context.dispatcher

  def receive = {
    case _ =>
      var heads  : ArrayBuffer[Html] = new ArrayBuffer[Html]()
      var bodies : ArrayBuffer[Html] = new ArrayBuffer[Html]()

      val futureList = Future.traverse((1 to modules.length).toList)(x => (moduleRepository ? ModuleRequest(modules(x-1))).mapTo[ModuleHTML])
      futureList.map { list =>
        list.foreach{ module =>
            println("received module " + module.head)
            heads.append(module.head)
            bodies.append(module.body)
        }
        println("received all modules: " + heads.length)
        sender ! render(PageHTML(heads.toList, bodies.toList))
      }
      println("i'm i assync?")
  }
}

class IndexActor extends PageActor {
  var modules = List("infobar", "header", "maincontainer", "footer")
  var pageName = "Index"
  def render(page: PageHTML) : Html = {
    html.index_layout.render(page.heads, page.bodies)
  }
}

class ExchangeActor extends PageActor {
  var modules = List("infobar")
  var pageName = "Exchange"

  def render(page: PageHTML) : Html = {
    html.exchange_layout.render(page.heads, page.bodies)
  }
}

