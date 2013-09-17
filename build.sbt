name := "play-sbt-flyway-plugin"

sbtPlugin := true

organization := "net.kamekoopa"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

// scalacOptions += "-feature"

libraryDependencies ++= Seq(
  "org.scalatest"         %% "scalatest"      % "1.9.1" % "test",
  "com.typesafe"          %  "config"         % "1.0.2" % "compile",
  "com.googlecode.flyway" % "flyway-core"     % "2.1.1" % "compile"
)

initialCommands := "import net.kamekoopa.play.plugin.sbt.flyway._"

