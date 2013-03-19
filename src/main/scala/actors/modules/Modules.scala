package actors.modules

import akka.actor.Actor
import common.Messages.{ModuleJsRequest, RenderedModule, ModuleHTML}
import spray.json._
import DefaultJsonProtocol._


trait ModuleActor extends Actor {
  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  val data : ModuleHTML
  def receive = {
    case name : String => {
      l("rendering...")
      sender ! RenderedModule(name, data)
    }
    case ModuleJsRequest(mod, path) => {
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

class Header extends ModuleActor {
  val TAG = "[HeaderActor] "
  val data : ModuleHTML = ModuleHTML(html.header.render(), html.header_head.render("header.css", "header.js"))
}

class Sidebar extends ModuleActor {
  val TAG = "[SidebarActor] "
  val data : ModuleHTML = ModuleHTML(html.sidebar.render("Bob", 42), html.header_head.render("sidebar.css", "sidebar.css"))
}

class Infobar extends ModuleActor {
  val TAG = "[InfobarActor] "
  val data : ModuleHTML = ModuleHTML(html.infobar.render(), html.infobar_head.render("infobar.css", "infobar.js"))
}

class Footer extends ModuleActor {
  val TAG = "[FooterActor] "
  val data : ModuleHTML = ModuleHTML(html.footer.render(), html.footer_head.render("footer.css", "footer.js"))
}

class MainContainer extends ModuleActor {
  val TAG = "[MainContainerActor] "
  val data : ModuleHTML = ModuleHTML(html.maincontainer.render(), html.maincontainer_head.render("maincontainer.css", "maincontainer.js"))
}