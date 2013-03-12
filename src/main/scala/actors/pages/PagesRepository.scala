package actors.pages

import akka.actor.{Props, ActorRef, Actor}
import akka.util.Timeout
import akka.routing.RoundRobinRouter
import common.messages.PageRequest
import akka.pattern._
import twirl.api.Html
import concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class PagesRepository extends Actor {
  implicit val timeout = Timeout(5000)
  var pages : List[ActorRef] = List(
    context.actorOf(Props[IndexActor].withRouter(RoundRobinRouter(nrOfInstances = 1))),
    context.actorOf(Props[ExchangeActor].withRouter(RoundRobinRouter(nrOfInstances = 1)))
  )

  def receive = {
    case PageRequest("index") => (pages(0) ? "index").mapTo[Html] pipeTo sender
    case PageRequest(_)       => (pages(1) ? "exchange").mapTo[Html] pipeTo sender
  }
}
