package actors.pages

import akka.actor._
import common.Messages._
import akka.pattern._
import akka.util.Timeout
import concurrent.Await
import twirl.api.Html
import collection.mutable.ArrayBuffer
import akka.actor.ActorRef
import collection.mutable
import spray.util.SprayActorLogging
import spray.util._
import com.typesafe.config.{ConfigFactory, Config}
import actors.pages.interface.PageActor



class IndexActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo, "pages/index_page.conf") {
  val TAG = "[IndexActor] "

  var pageName = "Index"
  def render(page: PageHTML) : Html = {
    html.index_layout.render(page.heads, page.bodies)
  }
}

class ExchangeActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo, "pages/exchange_page.conf") {
  val TAG = "[ExchangeActor] "
  var pageName = "Exchange"

  def render(page: PageHTML) : Html = {
    html.exchange_layout.render(page.heads, page.bodies)
  }
}