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


abstract class PageActor(pagesRepo: ActorRef, modulesRepo: ActorRef, configPath: String) extends Actor with SprayActorLogging {

  /* DEBUGGING */
  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  import context.dispatcher
  implicit val timeout = Timeout(5000)

  var modulesMap : mutable.LinkedHashMap[String, ModuleHTML] = new mutable.LinkedHashMap[String, ModuleHTML]
  var modules  = ConfigFactory.load(configPath).getStringList("modules")

  var pageName   : String
  def render(page : PageHTML) : Html

  def receive = {
    case p : String => {

      val it = modules.iterator()
      while(it.hasNext()){
        val m = it.next()
        var future = modulesRepo ? ModuleHTMLRequest(m)
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


//      val futureList = Future.traverse((1 to modules.length).toList)(x => (moduleRepository ? ModuleHTMLRequest(modules(x-1))).mapTo[ModuleHTML])
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

  def allRendered() : Boolean = modulesMap.size.equals(modules.size)

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

class IndexActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo, "index_page.conf") {
  val TAG = "[IndexActor] "

  var pageName = "Index"
  def render(page: PageHTML) : Html = {
    html.index_layout.render(page.heads, page.bodies)
  }
}

class ExchangeActor(pagesRepo: ActorRef, modulesRepo: ActorRef) extends PageActor(pagesRepo, modulesRepo, "exchange_page.conf") {
  val TAG = "[ExchangeActor] "
  var pageName = "Exchange"

  def render(page: PageHTML) : Html = {
    html.exchange_layout.render(page.heads, page.bodies)
  }
}