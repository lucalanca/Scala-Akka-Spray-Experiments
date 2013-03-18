package actors.cougar
import akka.actor.Actor

trait CougarClient extends Actor {}

class EROClient extends CougarClient {
  def receive = {
    case _ => println("YEAH")
  }
}


