package com.caidt

import akka.actor.ActorRef
import com.caidt.share.*
import com.google.protobuf.MessageLite


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}

/** 向其他玩家发送消息 */
fun PlayerActor.sendToPlayer(playerId: Long, msg: Any) {
  sendMessageToHome(PlayerEnvelope(playerId, msg))
}

fun PlayerActor.answer() {

}

// fixme: 真的需要这个方法吗？
fun PlayerActor.sendMessage(msg: Any) {
  when (msg) {
    is MessageLite -> sendToClient(msg)
    is PlayerMessage -> sendMessageToHome(msg)
    is PlayerEnvelope -> sendMessageToHome(msg)
    is WorldMessage -> sendMessageToWorld(msg)
    is WorldEnvelope -> sendMessageToWorld(msg)
    else -> unhandled(msg)
  }
}

internal fun PlayerActor.sendMessageToHome(msg: PlayerMessage) {
  sendMessageToHome(msg.wrap())
}

internal fun PlayerActor.sendMessageToHome(msg: PlayerEnvelope) {
  Home.homeProxy.tell(msg, self)
}

internal fun PlayerActor.sendMessageToWorld(msg: WorldMessage) {
  sendMessageToWorld(msg.wrap())
}

internal fun PlayerActor.sendMessageToWorld(msg: WorldEnvelope) {
  Home.worldProxy.tell(msg, self)
}

internal fun selection(request: Any): ActorRef {
  return when (request) {
    is PlayerMessage -> Home.homeProxy
    is WorldMessage -> Home.worldProxy
    else -> throw RuntimeException()
  }
}
