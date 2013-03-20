package domain.runners

trait IRunnerDescription {

  val metadata    : Map[String,String]
  val runnerName  : String

}
