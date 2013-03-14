package common

import twirl.api.Html
import collection.mutable.ArrayBuffer

object Messages {
  case class PageRequest(path: String)
  case class ModuleRequest(name: String)
  case class ModuleHTML(body: Html, head: Html)
  case class PageHTML(heads: List[Html], bodies: List[Html])
  case class RenderedModule(name: String, rendered: ModuleHTML)





  /*TODO: DYNAMIC BEHAVIOUR*/
  case class AddPage()
  case class AddModule()
  case class ChangePage()
  case class ChangeModule()
}
