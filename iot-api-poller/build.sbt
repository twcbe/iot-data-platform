name := "iot-api-poller"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalamock" %% "scalamock" % "4.4.0" % Test
)