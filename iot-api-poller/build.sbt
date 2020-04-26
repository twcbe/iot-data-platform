name := "iot-api-poller"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.scalamock" %% "scalamock" % "4.4.0" % Test
)