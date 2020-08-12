package com.caidt

import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.ActorSelection
import com.caidt.share.NamedRunnable

/**
 * 连接各个模块，将事件发出 （可公用，该类无状态）
 */
class EventBus(context: ActorContext) {

  private val executor: ActorSelection = context.actorSelection("/user/worker")

  private fun fire(name: String, exec: () -> Unit) {
    executor.tell(NamedRunnable(name, Runnable(exec)), ActorRef.noSender())
  }

  fun firePowerChange(player: PlayerActor, power: Int) {
    fire("recordPowerChange") {

    }
    fire("record") {

    }
  }

}


// todo: 单人回档（仅个人数据可回档，全局数据和地图数据不可）