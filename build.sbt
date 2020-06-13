name := """authentication"""
organization := "com.manh"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

lazy val databaseDependencies = Seq(
  jdbc,
  "mysql" % "mysql-connector-java" % "5.1.44",
  "org.playframework.anorm" %% "anorm" % "2.6.5"
)

lazy val tokenDependencies = Seq(
  "com.pauldijou" %% "jwt-core" % "4.2.0"
)

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= databaseDependencies
libraryDependencies ++= tokenDependencies

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.manh.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.manh.binders._"
