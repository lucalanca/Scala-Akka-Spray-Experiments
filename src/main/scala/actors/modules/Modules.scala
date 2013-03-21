package actors.modules

import akka.actor.{Props, Actor}
import common.Messages.{ModuleJsonRequest, RenderedModule, ModuleHTML}
import spray.json._
import DefaultJsonProtocol._
import spray.client._
import actors.modules.interface.ModuleActor



class Header extends ModuleActor("modules/header.conf") {
  val TAG = "[HeaderActor] "
  val data : ModuleHTML = ModuleHTML(html.header.render(), html.header_head.render("header.css", "header.js"))
}

class Sidebar extends ModuleActor("modules/sidebar.conf") {
  val TAG = "[SidebarActor] "
  val data : ModuleHTML = ModuleHTML(html.sidebar.render("Bob", 42), html.header_head.render("sidebar.css", "sidebar.css"))
}

class Infobar extends ModuleActor("modules/infobar.conf") {
  val TAG = "[InfobarActor] "
  val data : ModuleHTML = ModuleHTML(html.infobar.render(), html.infobar_head.render("infobar.css", "infobar.js"))
}

class Footer extends ModuleActor("modules/footer.conf") {
  val TAG = "[FooterActor] "
  val data : ModuleHTML = ModuleHTML(html.footer.render(), html.footer_head.render("footer.css", "footer.js"))
}

class MainContainer extends ModuleActor("modules/maincontainer.conf") {
  val TAG = "[MainContainerActor] "
  val data : ModuleHTML = ModuleHTML(html.maincontainer.render(), html.maincontainer_head.render("maincontainer.css", "maincontainer.js"))
}

