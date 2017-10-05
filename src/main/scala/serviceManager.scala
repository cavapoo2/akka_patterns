package com.andyr
import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

import scala.concurrent.duration._

class ServicesManager(childrenCreator: ActorRef) extends Actor with ActorLogging {
  import OrderedKiller._
  import context._

  override def preStart() {
    log.info("Asking for my children")
    childrenCreator ! GetChildren(self)
  }

  def waiting: Receive = {
    case Children(kids) =>
      log.info(s"Children received ${kids.map(_.path.name)}")
      context.become(initialized(kids))
      context.system.scheduler.scheduleOnce(1 second, self, "something")
  }

  def initialized(kids: Iterable[ActorRef]) : Receive = {
    case GetChildren(parent) =>
      log.info("ServiceManager: Received GetChildren {}" ,parent)
      childrenCreator !  GetChildren(parent)
    case Children(childs) =>
      log.info(s"ServiceManager: Received Children ${childs.map(_.path.name)}")
    case Specific(name) =>
      log.info(s"name is $name")
      val kid = kids.filter(k => k.path.name == name)
      if (!kid.isEmpty) {
        kid.head ! "hello"
      }
    case _ =>
      log.info(s"I have been happily initialized with my kids: ${kids.map(_.path.name)}")
  }

  def receive = waiting
  override def postStop() = log.info(s"${self.path.name} has stopped")
}