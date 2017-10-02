package com.andyr
import java.util.UUID

import akka.actor.{ActorSystem, Props}

object EnvelopingActorApp extends App {
  //useful where you need to add more content to a message before sending it on to another actor ?
  val actorSystem = ActorSystem()
  val envelopReceived = actorSystem.actorOf(Props[EnvelopeReceiver], "receiver")
  val envelopingActor = actorSystem.actorOf(Props(classOf[EnvelopingActor], envelopReceived, headers _))
  envelopingActor ! "Hello!"

  def headers(msg: Any) = {
    Map(
      "timestamp" -> System.currentTimeMillis(),
      "correlationId" -> UUID.randomUUID().toString
    )
  }
}