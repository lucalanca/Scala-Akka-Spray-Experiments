package common

import twirl.api.Html

object messages {
  case class PageRequest(path: String)
  case class ModuleRequest(name: String)
  case class HTMLPage(root: twirl.api.Html)
}
