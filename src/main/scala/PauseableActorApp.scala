package com.andyr
import akka.actor.{ActorSystem, Props}
import com.andyr.PausableActor.Work

object PausableActorApp extends App {
  val actorSystem = ActorSystem()
  val hardWorker = actorSystem.actorOf(Props[HardWorker], "hardWorker")
  val pausableHardWorker = actorSystem.actorOf(Props(classOf[PausableActor], hardWorker), "pausableActor")

  (1 to 100).foreach { i =>
    pausableHardWorker ! Work(i)
  }
}