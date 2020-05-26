package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.caidt.share.NamedRunnable
import com.caidt.share.Worker
import com.caidt.share.tellNoSender

/**
 * 连接各个模块，将事件发出 （可公用，该类无状态）
 */
class EventBus(actorSystem: ActorSystem) {

  private val executor: ActorRef = actorSystem.actorOf(Worker.props())

  private fun fire(name: String, exec: () -> Unit) {
    executor.tellNoSender(NamedRunnable(name, Runnable(exec)))
  }

  fun firePowerChange(player: PlayerActor, power: Int) {
    fire("recordPowerChange") {

    }
    fire("record") {

    }
  }

}


// todo: 单人回档（仅个人数据可回档，全局数据和地图数据不可）