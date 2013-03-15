organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.10.0"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "sprest snapshots" at "http://markschaake.github.com/snapshots"
)

libraryDependencies ++= Seq(
  "io.spray"                %   "spray-can"                 % "1.1-M7"           ,
  "io.spray"                %   "spray-routing"             % "1.1-M7"           ,
  "io.spray"                %   "spray-testkit"             % "1.1-M7"           ,
  "com.typesafe.akka"       %%  "akka-actor"                % "2.1.0"            ,
  "org.specs2"              %%  "specs2"                    % "1.13"     % "test",
  "org.scala-lang.plugins"  %   "continuations"             % "2.10.0"           ,
  "com.typesafe.akka"       %%  "akka-dataflow"             % "2.1.0"            ,
  "sprest"                  %% "sprest-core"                % "0.1.0-SNAPSHOT"   ,
  "com.typesafe.akka"       %% "akka-cluster-experimental"  % "2.2-SNAPSHOT"
)

autoCompilerPlugins := true

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

seq(coffeeSettings: _*)

seq(lessSettings:_*)
