package actors.pages

import akka.actor._
import common.Messages._
import akka.pattern._
import akka.util.Timeout
import utils.TimeKeeper
import concurrent.{Await, Future, ExecutionContext}
import actors.modules.{ModuleRepository, ModuleActor}
import scala.xml._
import twirl.api.Html
import collection.mutable.{ListBuffer, ArrayBuffer}
import akka.actor.ActorRef
import collection.mutable
import java.util
import spray.util.SprayActorLogging


abstract class PageActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends Actor with SprayActorLogging {
  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  import context.dispatcher
  implicit val timeout = Timeout(5000)

  var modulesMap : mutable.LinkedHashMap[String, ModuleHTML] = new mutable.LinkedHashMap[String, ModuleHTML]
  var modules : List[String]

  var pageName   : String

  def render(page : PageHTML) : Html



  def receive = {
    case p : String => {
      for(m <- modules) {
        var future = modulesRepo ? ModuleRequest(m)
        var result = Await.result(future, timeout.duration).asInstanceOf[RenderedModule]
        l("received" + result.name)
        if (!modulesMap.contains(result.name))  modulesMap.put(result.name, result.rendered)
      }
      var heads  : ArrayBuffer[Html] = new ArrayBuffer[Html]()
      var bodies : ArrayBuffer[Html] = new ArrayBuffer[Html]()
      for(m <- modulesMap.values.toList){
        heads.append(m.asInstanceOf[ModuleHTML].head)
        bodies.append(m.asInstanceOf[ModuleHTML].body)
      }
      sender ! render(PageHTML(heads.toList, bodies.toList))
    }


//      val futureList = Future.traverse((1 to modules.length).toList)(x => (moduleRepository ? ModuleRequest(modules(x-1))).mapTo[ModuleHTML])
//      futureList.map { list =>
//        list.foreach{ module =>
//            println("received module " + module.head)
//            heads.append(module.head)
//            bodies.append(module.body)
//        }
//        println("received all modules: " + heads.length)
//        sender ! render(PageHTML(heads.toList, bodies.toList))
//      }


  }
  def addRenderedModule(mod: RenderedModule) : Unit = {
    if (!modulesMap.contains(mod.name))  modulesMap.put(mod.name, mod.rendered)
  }

  def allRendered() : Boolean = modulesMap.size == modules.size

  def sendPage() : Unit = {
    var heads  : ArrayBuffer[Html] = new ArrayBuffer[Html]()
    var bodies : ArrayBuffer[Html] = new ArrayBuffer[Html]()
    for(m <- modulesMap.values.toList){
      heads.append(m.asInstanceOf[ModuleHTML].head)
      bodies.append(m.asInstanceOf[ModuleHTML].body)
    }
    l("Sending to : " + pagesRepo.path.toString)

    val html = render(PageHTML(heads.toList, bodies.toList))
    pagesRepo ! html
  }
}

class IndexActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo) {
  val TAG = "[IndexActor] "

  var modules = List("infobar", "header", "maincontainer", "footer")
  var pageName = "Index"
  def render(page: PageHTML) : Html = {
    html.index_layout.render(page.heads, page.bodies)
  }
}

class ExchangeActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo) {
  val TAG = "[ExchangeActor] "

  var modules = List("infobar")
  var pageName = "Exchange"


  def render(page: PageHTML) : Html = {
    html.exchange_layout.render(page.heads, page.bodies)
  }
}

