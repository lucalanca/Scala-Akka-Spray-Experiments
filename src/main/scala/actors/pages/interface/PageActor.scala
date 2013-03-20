package actors.pages.interface

import akka.actor.{Actor, ActorRef}
import spray.util.SprayActorLogging
import akka.util.Timeout
import collection.mutable
import common.Messages.{RenderedModule, ModuleHTMLRequest, PageHTML, ModuleHTML}
import com.typesafe.config.ConfigFactory
import concurrent.Await
import collection.mutable.ArrayBuffer
import twirl.api.Html
import akka.pattern._


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
        var future = modulesRepo ? ModuleHTMLRequest(m, 10)
        var result = Await.result(future, timeout.duration).asInstanceOf[RenderedModule]
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

    val html = render(PageHTML(heads.toList, bodies.toList))
    pagesRepo ! html
  }
}