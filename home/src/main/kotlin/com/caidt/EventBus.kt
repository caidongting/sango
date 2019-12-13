package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import com.caidt.infrastructure.LARGE_MAILBOX
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class NamedRunnable(val name: String, val exec: Runnable)

class JobActor : UntypedAbstractActor() {

  companion object {
    fun props(): Props {
      return Props.create { JobActor() }.withMailbox(LARGE_MAILBOX)
    }
  }

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  override fun onReceive(message: Any?) {
    when (message) {
      is NamedRunnable -> message.exec.run()
    }

  }

}

class EventBus(actorSystem: ActorSystem) {

  private val executor: ActorRef = actorSystem.actorOf(JobActor.props())

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
