package actors.cougar
import akka.actor.{Props, Actor}
import spray.can.client.HttpClient
import spray.io.IOExtension
import akka.util.Timeout
import concurrent.{Await, Future}
import spray.http.{HttpRequest, HttpResponse}
import spray.http.HttpMethods._
import spray.http.HttpResponse


import spray.can.client.{HttpDialog, HttpClient}
import concurrent.{Await, Future}
import spray.http.{HttpRequest, HttpResponse}
import spray.http.HttpMethods._
import spray.http.HttpResponse
import spray.client.HttpConduit



import spray.io.IOExtension
import concurrent.{Await, Future}
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.HttpMethods._
import akka.util.Timeout
import akka.pattern._

import spray.httpx.SprayJsonSupport._
import common.Messages.ModuleJsonRequest

trait CougarClient extends Actor {

  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  val host : String = "uk-api.betfair.com"
  val actions : List[String] = List("/www/sports/exchange/readonly/v1.0/bymarket")

  implicit val timeout = Timeout(5000)
  val ioBridge = IOExtension(context.system).ioBridge()
  val httpClient = context.actorOf(Props(new HttpClient(ioBridge)))

  val conduit = context.actorOf(Props(new HttpConduit(httpClient, host)))
  var pipeline = HttpConduit.sendReceive(conduit)

  def receive = {
    case ModuleJsonRequest(module, path, params, timestamp) => {
      l("received module json request")
      val response: Future[HttpResponse] = pipeline(HttpRequest(method = GET,uri = path+params)).mapTo[HttpResponse]
      val result = Await.result(response, timeout.duration).asInstanceOf[HttpResponse]
      l("received: "+result)
      l("will send to : " + sender.path.name)
      sender ! result
    }
  }
}

class EROClient extends CougarClient {
  val TAG = "[EROClient] "
}
class FOBClient extends CougarClient { val TAG = "[EROClient] " }
class ASClient extends CougarClient { val TAG = "[EROClient] " }


