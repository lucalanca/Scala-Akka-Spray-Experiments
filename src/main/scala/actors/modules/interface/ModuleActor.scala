package actors.modules.interface

import akka.actor._
import common.Messages._
import spray.json._
import DefaultJsonProtocol._
import com.typesafe.config.ConfigFactory
import akka.util.Timeout
import actors.cougar.CougarClientsRepository
import spray.http.HttpRequest
import akka.pattern._
import spray.http.HttpResponse
import spray.http.HttpResponse
import spray.http.HttpResponse
import common.Messages.ModuleJsonRequest
import common.Messages.RenderedModule
import common.Messages.ModuleHTML

abstract class ModuleActor(configPath: String) extends Actor {
  import context.dispatcher
  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  val usedServices = ConfigFactory.load(configPath).getStringList("services")
  implicit val timeout = Timeout(5000)

  val data : ModuleHTML
  def receive = {
    case name : String => sender ! RenderedModule(name, data)
    case ModuleJsonRequest(mod, "hello", _, _) => sender ! JsObject( "hello" -> JsString("world")).toString
    case ModuleJsonRequest(mod, "yabadaba", _, _) => sender ! JsObject( "yabadaba" -> JsString("doo")).toString
    case ModuleJsonRequest(mod, "ero", _, _) => {
      l("asking for ero!")
      var cougarRepo : ActorRef = context.actorOf(Props[CougarClientsRepository], "cougar-repo")
      (cougarRepo ? "yeah").mapTo[HttpResponse] pipeTo  sender
    }
    case ModuleJsonRequest(mod, path, params, _) => {
      sender ! JsObject(
        "path" -> JsString(path),
        "requested module" -> JsString(mod),
        "processing actor" -> JsString(TAG),
        "system uptime" -> JsString(context.system.uptime.toString),
        "start time" -> JsString(context.system.startTime.toString),
        "red" -> JsNumber(123),
        "green" -> JsNumber(11),
        "blue" -> JsNumber(44),
        "numbers" -> List(1,2,3).toJson
      ).toString
    }
  }
}


object HeaderStarted {

  def main(args: Array[String]): Unit = {
    val config =
      (if (args.nonEmpty) ConfigFactory.parseString(s"akka.remote.netty.tcp.port=${args(0)}")
      else ConfigFactory.empty).withFallback(
        ConfigFactory.parseString("akka.cluster.roles = [backend]")).
        withFallback(ConfigFactory.load())
    println("config: " + config.toString)
    println("args0: " + args(0))
    println("creating actor system")
    //val system = ActorSystem("ClusterSystem", config)
    val system = ActorSystem("[akka://ClusterSystem@127.0.0.1:2552")
    println("system: " + system.toString)
  }



}

