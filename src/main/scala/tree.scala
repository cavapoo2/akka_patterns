package com.andyr.Tree
import Messages._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}
import akka.pattern.ask
import akka.util.Timeout
import scala.util.Random
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Messages {
  case class PassItOn(slave:ActorRef)
  case object Work
  case object Cancel
}

class Watch(ceo:ActorRef) extends Actor with ActorLogging {
  import context._
   def receive: Receive = messages
   override def preStart(): Unit = {
     log.info(s"${self.path.name} is running")
     watch(ceo)
   }
  def messages:Receive = {
    case Terminated(actor) =>
      log.info(s"Terminated $actor")
  }
   override def postStop() = log.info(s"${self.path.name} has stopped")
}

class CEO extends Actor with ActorLogging {
  import Messages._
  import context._
  override def preStart(): Unit = {
     log.info(s"${self.path.name} is running")
    context.actorOf(Props[MVP], "Sales")
    context.actorOf(Props[MVP], "Engineering")
    context.actorOf(Props[MVP], "Marketing")
    context.actorOf(Props[MVP], "HR")
  }
    def receive = init

    def init: Receive = {
      case PassItOn(slave) =>
        children.foreach(_ ! PassItOn(slave))
      case Cancel
    }

  override def postStop() = log.info(s"${self.path.name} has stopped")
}

class MVP extends Actor with ActorLogging {
import context._
  def receive = init

  def init: Receive = {
    case PassItOn(slave) =>
      children.foreach((_ ! PassItOn(slave)))
  }
  override def preStart() = {
    log.info(s"${self.path.name} is running")
    context.actorOf(Props[Manager], "Manager")
  }
  override def postStop() = log.info(s"${self.path.name} has stopped")
}

class Manager extends Actor with ActorLogging {
  import context._
  def receive = enter

  def enter: Receive = {
    case PassItOn(slave) => slave ! Work

  }
   override def preStart() ={
     log.info(s"${self.path.name} is running")
   }

  override def postStop() = log.info(s"${self.path.name} has stopped")
}
class Slave extends Actor with ActorLogging {
  def receive = enter
  var countjobs: Int = 0
   override def preStart() ={
     log.info(s"${self.path.name} is running")
   }
  def enter:Receive = {
    case Work =>
      countjobs = countjobs + 1
      Thread.sleep(Random.nextInt(3)*1000)
      log.info(s"Jobcount is $countjobs")
  }
  override def postStop() = log.info(s"${self.path.name} has stopped")
}

object Workers extends App {
  import Messages._
 // import akka.system.dispatcher
  val actorSystem = ActorSystem()
//  implicit val ec =  actorSystem.dispatcher
 // implicit val timeout = Timeout(5 seconds) // needed for `?` below
  val ceo = actorSystem.actorOf(Props[CEO], "CEO")
  val watcher = actorSystem.actorOf(Props(classOf[Watch],ceo),"Watch")
  val slave = actorSystem.actorOf(Props[Slave],"Slave")
  val jobs = (1 to 10).toList.map(_ => PassItOn(slave))
  jobs.foreach(job => ceo ! job)
  ceo ! Cancel

  //val f  = ceo ? GetMVPs
/*
  f onComplete {
    case Success(mvps) =>
     // for (k <- kids.children) println(k)
      //println(kids)
      for (k <- mvps.asInstanceOf[MVPS].children)
        println(s"mvp=$k")
    case Failure(e) => println(e)
  }*/
  //Thread.sleep(4000)
//  ceo ! PoisonPill
//  ceo ! Cancel


//  Await.result(f,10 seconds)

Thread.sleep(100000)
  Await.ready(actorSystem.terminate(), 1.seconds)

}








/*
  class Manager extends Actor with ActorLogging {

    def receive: Receive = ???
  }

  class Worker extends Actor with ActorLogging {

    def receive: Receive = ???
  }*/


