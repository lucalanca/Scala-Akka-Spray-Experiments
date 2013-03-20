package actors.pages

import akka.actor.{Props, ActorRef, Actor}
import akka.util.Timeout
import common.Messages.{ChangePage, AddPage, RenderedModule, PageRequest}
import akka.pattern._
import twirl.api.Html
import actors.modules._
import collection.mutable

class PagesRepository(modulesRepo: ActorRef) extends Actor {

  val TAG = "[PagesRepository] "
  def l(s: String) : Unit = { println(TAG+s) }

  import context.dispatcher

  import context.dispatcher

  implicit val timeout = Timeout(5000)

  override def preStart() = {
    initializePages()
  }
  var pagesRefMap = mutable.LinkedHashMap[String,ActorRef]()

  def initializePages() = {
    pagesRefMap.put("index"       , context.actorOf(Props(new IndexActor(self, modulesRepo))  , "index"))
    pagesRefMap.put("exchange"      , context.actorOf(Props(new ExchangeActor(self, modulesRepo)), "exchange"))
  }

  def receive = {
    case PageRequest(name, _) => {
      l("got request for " + name)
      (pagesRefMap.get(name)) match {
        case None         => l("couldn't find page " + name) // TODO SENT TO PARENT OR SOMETHING
        case Some(result) => (result ? name).mapTo[Html] pipeTo sender
      }
    }
    case AddPage()    => l("TBD")
    case ChangePage() => l("TBD")
  }
}
