package com.andyr
import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import akka.pattern._

import scala.concurrent.duration._
import scala.concurrent.Future

object OrderedKiller {
  case object AllChildrenStopped
  case class GetChildren(parentActor: ActorRef)
  case class Children(children: Iterable[ActorRef])
  case class Specific(name:String)
}

abstract class OrderedKiller extends Actor with ActorLogging {
  import OrderedKiller._
  import context._
 //here the acc p is future wrapping a AllChildren. we just cycle over each child which has its own future.
  //each child is passed to gracefulStop which outputs another future[boolean]. we finally for each chil
  def killChildrenOrderly(orderedChildren: List[ActorRef]): Future[Any] = {
    log.info("OK:killChildrenOrderly")
    orderedChildren.foldLeft(Future(AllChildrenStopped))(
      (p, child) => p.flatMap(_ => gracefulStop(child, 2 seconds).map(_ => AllChildrenStopped))
    )
  }

  def noChildrenRegistered: Receive = {
    case GetChildren(parentActor) =>
      watch(parentActor) //this actor will be terminated when its watched (or parent in this case)  Actor is terminated
      parentActor ! Children(children)
      become(childrenRegistered(parentActor))
      log.info("OrderedKiller:Rec Not Registered yet!")
  }

  def childrenRegistered(to: ActorRef): Receive = {
    case GetChildren(parentActor) if sender == to =>
      log.info("OK:childrenRegistered: {}", parentActor )
      parentActor ! Children(children)
    //case Terminated(`to`) =>
    case Terminated(`to`) =>
      log.info("OK Terminated: {}", to)
      killChildrenOrderly(orderChildren(children)) pipeTo self
    case AllChildrenStopped =>
      log.info("AllChildrenStopped")
      stop(self)
  }

  def orderChildren(unorderedChildren: Iterable[ActorRef]) : List[ActorRef]

  def receive = noChildrenRegistered
}