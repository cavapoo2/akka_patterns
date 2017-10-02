package com.andyr
import akka.actor.{Actor, ActorRef}
import com.andyr.Envelope._

object Envelope {
  type Headers = Map[String, Any]
  case class Envelope[T](msg: T, headers: Headers = Map.empty)
}

class EnvelopingActor(nextActor: ActorRef, addHeaders: Any => Headers) extends Actor {
 // def this(nextActor: ActorRef) = this(nextActor, _ => Map())
  def receive = { case msg => nextActor ! new Envelope(msg, addHeaders(msg)) }
}