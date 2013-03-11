package actors.pages

import akka.actor._
import common.messages._
import akka.pattern._
import akka.util.Timeout
import utils.TimeKeeper
import concurrent.{Await, Future, ExecutionContext}
import ExecutionContext.Implicits.global
import actors.modules.{ModuleRepository, ModuleActor}
import javax.xml.bind.annotation.XmlAccessOrder
import scala.xml._

//Possible code smell
import akka.routing._
import akka.actor.ActorRef

class PagesRepository extends Actor {
  implicit val timeout = Timeout(5000)
  var pages : List[ActorRef] = List(
    context.actorOf(Props[IndexActor].withRouter(RoundRobinRouter(nrOfInstances = 100))),
    context.actorOf(Props[ExchangeActor].withRouter(RoundRobinRouter(nrOfInstances = 100)))
  )

  def receive = {
    case PageRequest("index") => (pages(0) ? "index").mapTo[HTMLPage] pipeTo sender
    case PageRequest(_)       => (pages(1) ? "exchange").mapTo[HTMLPage] pipeTo sender
  }
}

class IndexActor extends PageActor {
  var modules = List("header")
  var pageName = "Index"
}

class ExchangeActor extends PageActor {
  var modules = List("header", "sidebar")
  var pageName = "Exchange"
}

trait PageActor extends Actor {
  implicit val timeout = Timeout(5000)
  var modules : List[String]
  var moduleRepository : ActorRef = context.actorOf(Props[ModuleRepository])
  var pageName   : String
  // TODO: Add a layout and a better way of rendering pages

  def receive = {

    case _ =>
      var builder = StringBuilder.newBuilder
      builder.append("<section>")
      for(m <- modules) {
        // TODO: load modules assynchrounously instead of blocking the calls
        var future : Future[HTMLPage] = (moduleRepository ? ModuleRequest(m)).mapTo[HTMLPage]
        var result = Await.result(future, timeout.duration)
        builder.append(result.root.toString)
      }
      builder.append("</section>")
      System.out.println(html.header.render("Bob", 42))
      sender ! HTMLPage(twirl.api.Html(builder.toString()))

  }
}