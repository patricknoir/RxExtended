name := "rxExtended"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
  "com.netflix.rxjava" % "rxjava-scala" % "0.20.7"
)
    