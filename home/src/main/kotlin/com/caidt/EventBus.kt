package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import com.caidt.infrastructure.LARGE_MAILBOX
import com.caidt.share.tellNoSender
import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext


class NamedRunnable(val name: String, val exec: Runnable)

class JobActor : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  companion object {
    fun props(): Props {
      return Props.create { JobActor() }.withMailbox(LARGE_MAILBOX)
    }
  }

  override fun onReceive(message: Any?) {
    when (message) {
      is NamedRunnable -> message.exec.run()
    }

  }

}

class EventBus(actorSystem: ActorSystem) {

  private val executor: ActorRef = actorSystem.actorOf(JobActor.props())
  // private val coroutineContext: CoroutineContext = CoroutineContext()

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
