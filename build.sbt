ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "exchange_rate_api",
    idePackagePrefix := Some("org.formedix.exchange_rate_api")
  )

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "joda-time" % "joda-time" % "2.9.9"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test
)
libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3"

