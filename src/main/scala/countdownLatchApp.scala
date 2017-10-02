package com.andyr
import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinPool
//this recives messages until a specific count, then it resumes and does whatever is needed after count is reached
object CountDownLatchApp extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem._
  val routeesToSetUp = 10
  val countDownLatch = CountDownLatch(routeesToSetUp) // a class with inbuilt actor and future handling
  //we have one CountDownLatch but 10 CountDownLatchWorkers
  actorSystem.actorOf(Props(classOf[CountDownLatchWorker], countDownLatch)   //worker actor that uses it
    .withRouter(RoundRobinPool(routeesToSetUp)), "workers")

  //Future based solution
  countDownLatch.result.onSuccess { case _ => log.info("Future completed successfully") }

  //Await based solution
  countDownLatch.await()
  actorSystem.terminate()
}