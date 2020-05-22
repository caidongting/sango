package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.caidt.share.NamedRunnable
import com.caidt.share.Worker
import com.caidt.share.tellNoSender

class EventBus(actorSystem: ActorSystem) {

  private val executor: ActorRef = actorSystem.actorOf(Worker.props())

  private fun fire(name: String, exec: () -> Unit) {
    executor.tellNoSender(NamedRunnable(name, Runnable { exec() }))
  }

  fun firePowerChange(player: PlayerActor, power: Int) {
    fire("recordPowerChange") {

    }
    fire("record") {

    }
  }

}
