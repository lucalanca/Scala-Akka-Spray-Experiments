package domain.runners

trait IRunner {
  var description     : IRunnerDescription
  var handicap        : Integer
  var selectionId     : Integer
  var exchange        : IExchange
  var state           : IRunnerState
}
