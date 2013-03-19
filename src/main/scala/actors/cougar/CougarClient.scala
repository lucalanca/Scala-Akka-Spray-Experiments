package actors.cougar
import akka.actor.{Props, Actor}
import spray.can.client.HttpClient
import spray.io.IOExtension

trait CougarClient extends Actor {
  val ioBridge = IOExtension(context.system).ioBridge()
  val httpClientx = context.actorOf(Props(new HttpClient(ioBridge)), "http-client")

  def receive = {
    case _ => println("YEAH")
  }
}

class EROClient extends CougarClient {}
class FOBClient extends CougarClient {}
class ASClient extends CougarClient {}


