package common

import twirl.api.Html
import collection.mutable.ArrayBuffer

object messages {
  case class PageRequest(path: String)
  case class ModuleRequest(name: String)
  case class ModuleHTML(body: Html, head: Html)
  case class PageHTML(heads: List[Html], bodies: List[Html])
}
