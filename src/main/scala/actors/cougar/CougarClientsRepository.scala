package actors.cougar

import akka.actor.{Props, ActorRef, Actor}
import collection.mutable
import akka.pattern.ask
import scala.util.{Success, Failure}
import spray.can.client.HttpClient
import spray.io.IOExtension
import spray.http._
import spray.util._
import HttpMethods.GET
import scala.util.{Success, Failure}
import scala.concurrent.duration._
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import spray.can.client.HttpClient
import spray.httpx.SprayJsonSupport
import spray.http._
import HttpMethods.GET
import spray.http
import common.Messages.{ModuleJsonRequest, RenderedModule}
import akka.pattern._
import concurrent.Await

class CougarClientsRepository extends Actor {

  val TAG : String = "[CougarRepo] "
  def l(s: String) : Unit = { println(TAG+s) }
  implicit val timeout: Timeout = 5 seconds span

  override def preStart() = {
    initializeCougarClients()
  }

  var httpClient : ActorRef = self
  var cougarRefMap = mutable.LinkedHashMap[String,ActorRef]()


  def initializeCougarClients() = {
    cougarRefMap.put("ERO"   , context.actorOf(Props[EROClient], "ero-client"))


  }
  def receive = {
    case m => {
      l("someone wants something")
      val myPath = "/www/sports/exchange/readonly/v1.0/bymarket"
      val params = "?currencyCode=GBP&alt=json&locale=en_GB&types=MARKET_STATE,MARKET_RATES,MARKET_DESCRIPTION,EVENT,RUNNER_DESCRIPTION,RUNNER_STATE,RUNNER_EXCHANGE_PRICES_BEST,RUNNER_METADATA&marketIds=1.108633981&ts=1363711129952"
      (cougarRefMap.get("ERO")) match {
        case None         => println("couldn't find ERO")
        case Some(result) => {
          l("found ERO")
          var future = (result ? ModuleJsonRequest("ERO", myPath, params, 20))
          l("received future: " + future)
          var response = Await.result(future, timeout.duration).asInstanceOf[HttpResponse]
          l("received something" + response)
          sender ! response
        }
      }
    }
  }
}