name := "akka-typed-fundamentals"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor-typed"         % akkaVersion,
  "ch.qos.logback"             % "logback-classic"           % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.2",
  "com.typesafe.akka"          %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka"          %% "akka-testkit"             % akkaVersion % Test,
  "org.scalatest"              %% "scalatest"                % "3.1.0" % Test,
  "com.typesafe.akka"          %% "akka-slf4j"               % "2.6.3" % Test // only for untyped
)

parallelExecution in Test := false
