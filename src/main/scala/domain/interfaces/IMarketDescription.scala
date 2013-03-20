package domain.interfaces

import javax.management.StringValueExp

trait IMarketDescription {

  val bettingType         : String
  val bspMarket           : Boolean
  val marketName          : String
  val marketTime          : String
  val marketType          : String
  val persistenceEnabled  : Boolean
  val suspendTime         : String
  val turnInPlayEnabled   : Boolean

}
