package com.andyr
import akka.actor.Actor
import com.andyr.Reaper.WatchMe

trait ReaperAwareActor extends Actor {
  override final def preStart() = {
    registerReaper()
    preStartPostRegistration()
  }

  private def registerReaper() = {
    context.actorSelection("/user/Reaper") ! WatchMe(self)
  }

  def preStartPostRegistration() : Unit = ()
}