package com.andyr
import akka.actor.{ActorSystem, PoisonPill, Props}
import com.andyr.OrderedKiller.{GetChildren, Specific}

import scala.concurrent.duration._
import scala.concurrent.Await

object OrderedTerminationApp extends App {

  val actorSystem = ActorSystem()
  val orderedKiller = actorSystem.actorOf(Props[ServiceHandlersCreator], "serviceHandlersCreator")
  val servicesManager = actorSystem.actorOf(Props(classOf[ServicesManager], orderedKiller), "servicesManager")

  Thread.sleep(2000)
  servicesManager ! GetChildren(servicesManager)
  Thread.sleep(2000)

  servicesManager ! Specific("DatabaseHandler1")
  Thread.sleep(2000)

  servicesManager ! PoisonPill //this will send Terminated to Ordered Killer since its watcing serviceManager
  Thread.sleep(10000)

 // actorSystem.stop(servicesManager)
  Await.ready(actorSystem.terminate(), 10.seconds)
}