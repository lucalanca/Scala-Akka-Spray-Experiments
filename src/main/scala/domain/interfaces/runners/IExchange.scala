package domain.runners

trait IExchange {

  val availableToBack : List[IExchangeSlot]
  val availabletoLay  : List[IExchangeSlot]

  case class IExchangeSlot(price: Int, size: Int)

}
