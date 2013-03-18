package actors.modules

import akka.actor.Actor
import common.Messages.{ModuleJsRequest, RenderedModule, ModuleHTML}


trait ModuleActor extends Actor {
  val TAG : String
  def l(s: String) : Unit = { println(TAG+s) }

  val data : ModuleHTML
  def receive = {
    case name : String => {
      l("rendering...")
      sender ! RenderedModule(name, data)
    }
    case ModuleJsRequest(_, path) => {
      sender ! "{'hello': 'world'}"
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