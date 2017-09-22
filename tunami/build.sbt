name := """tunami"""
organization := "com.tunami"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += guice

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.8"

libraryDependencies ++= Seq( 
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1"
)

libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += specs2 % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.tunami.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.tunami.binders._"

//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

