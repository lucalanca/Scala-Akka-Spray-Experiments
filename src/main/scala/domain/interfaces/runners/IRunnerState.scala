package domain.runners

trait IRunnerState {

  val adjustmentFactor : Float
  val sortPriority     : Integer
  val status           : String
  val totalMatched     : Integer

}
