package common

import twirl.api.Html
import collection.mutable.ArrayBuffer
import collection.immutable.HashMap
import spray.json._
import DefaultJsonProtocol._


object Messages {
  case class PageRequest(path: String, timestamp: Long)
  case class ModuleHTMLRequest(name: String, timestamp: Long)
  case class ModuleJsonRequest(moduleId: String, path: String, params: String, timestamp: Long)



  case class PageHTML(heads: List[Html], bodies: List[Html])
  case class ModuleHTML(body: Html, head: Html)
  case class ModuleJson(data: JsObject)


  case class RenderedModule(name: String, rendered: ModuleHTML)


  /*TODO: DYNAMIC BEHAVIOUR*/
  case class AddPage()
  case class AddModule()
  case class ChangePage()
  case class ChangeModule()
}
