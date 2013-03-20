package domain.interfaces

import domain.runners.IRunner

trait IMarket {

  val description           : IMarketDescription
  val isMarketDataDelayed   : Boolean
  val marketId              : String
  val rates                 : IRate
  val runners               : List[IRunner]

}
