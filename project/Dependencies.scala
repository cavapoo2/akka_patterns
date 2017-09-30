import sbt._
import Keys._
object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
  val akka = "com.typesafe.akka" %% "akka-actor" % "2.5.6"
  val akka_contrib =  "com.typesafe.akka" % "akka-contrib_2.12" % "2.5.6"
}
