package actors.modules

import akka.actor._
import common.Messages._
import akka.util.Timeout
import concurrent.ExecutionContext
import twirl.api.Html
import common.Messages.ModuleHTMLRequest
import scala.Some
import spray.routing.HttpServiceActor
import collection.mutable
import common.ModuleType
import akka.cluster._
import akka.cluster.ClusterEvent._
import common.Messages.ChangeModule
import common.Messages.ModuleJsonRequest
import common.Messages.ModuleHTMLRequest
import common.Messages.AddModule
import scala.Some
import common.ModuleType

import akka.contrib.pattern.ClusterSingletonManager
import com.typesafe.config.ConfigFactory


class ModuleRepository extends Actor with HttpServiceActor with ActorLogging {
  import scala.collection.mutable.Map
  import context.dispatcher
  implicit val timeout = Timeout(5000)

  var modules     = Map.empty[ModuleType, ActorRef]

  override def postStop(): Unit = Cluster(context.system).unsubscribe(self)

  override def preStart() = {
    initializeMockModules()
    Cluster(context.system).subscribe(self, classOf[LeaderChanged])
    l("i was started!!")
    l(self.path.toString)
  }

  def initializeMockModules() = {
    modules  = Map.empty[ModuleType, ActorRef]
    modules += (ModuleType("header")        -> context.actorOf(Props[Header] , "header_actor"))
    modules += (ModuleType("sidebar")       -> context.actorOf(Props[Sidebar] , "sidebar_actor"))
    modules += (ModuleType("infobar")       -> context.actorOf(Props[Infobar] , "infobar_actor"))
    modules += (ModuleType("footer")        -> context.actorOf(Props[Footer] , "footer_actor"))
    modules += (ModuleType("maincontainer") -> context.actorOf(Props[MainContainer] , "maincontainer_actor"))
  }

  def receive = {
    case ModuleHTMLRequest(name,_) => {
      (modules.get(ModuleType(name))) match {
          case None        => l("fuck off")  // TODO: some kind of error handling here
          case Some(actor) => actor forward name
      }
    }
    case ModuleJsonRequest(name, path, params, _) => {
      (modules.get(ModuleType(name))) match {
        case None        => l("fuck off")
        case Some(actor) => actor forward ModuleJsonRequest(name,path,params, 10)
      }
    }
    case AddModule()    => l("TBD")
    case ChangeModule() => l("TBD")
  }



  val TAG = "[ModuleRepository] "
  def l(s: String) : Unit = { println(TAG+s) }


}

object ModuleRepository  {





  def main(args: Array[String]): Unit = {
    val config =
      (if (args.nonEmpty) ConfigFactory.parseString("akka.remote.netty.tcp.port=${args(0)}")
      else ConfigFactory.empty).withFallback(
        ConfigFactory.parseString("akka.cluster.roles = [modulerepository]")).
        withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[ModuleRepository], "my-modulerepository")
    system.actorOf(Props(new ClusterSingletonManager(
      singletonProps = handOverData => Props[ModuleRepository],
      singletonName = "master",
      terminationMessage = PoisonPill)),
    name = "singleton")
  }
}
