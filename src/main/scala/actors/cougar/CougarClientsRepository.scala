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

class CougarClientsRepository extends Actor {
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
    case _ => {
      val responseFuture = httpClient.ask("http://www.google.com").mapTo[HttpResponse]
      responseFuture onComplete {
        case Success(response) =>
         println(
            """|Response for GET request to github.com:
              |status : {}
              |headers: {}
              |body   : {}""".stripMargin,
            response.status.value, response.headers.mkString("\n  ", "\n  ", ""), response.entity.asString
          )

        case Failure(error) =>
          println(error, "Couldn't get http://github.com")

      }
    }
  }
}