package com.andyr
import akka.actor.{Actor, ActorLogging,ActorRef}

class ServiceHandler extends Actor with ActorLogging {
  //def receive = Actor.ignoringBehavior
  def receive = enter
  def enter: Receive = {
    case _ => log.info(s"Hello $self.path.name")
  }
  override def preStart() = log.info(s"${self.path.name} is running")
  override def postStop() = log.info(s"${self.path.name} has stopped")
}