package com.caidt

import com.caidt.share.*


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}

/** 向其他玩家发送消息 */
fun PlayerActor.sendToPlayer(playerId: Long, msg: Any) {
  val envelope = PlayerEnvelope(playerId, msg)
  Home.homeProxy.tell(envelope, self)
}

fun PlayerActor.answer() {

}

fun PlayerActor.sendMessage(msg: Any) {
  when (msg) {
    is PlayerEnvelope -> Home.homeProxy.tell(msg, self)
    is WorldEnvelope -> Home.worldProxy.tell(msg, self)
    is WorldMessage -> {
      val envelope = wrapWorldEnvelope(worldId, msg)
      Home.worldProxy.tell(envelope, self)
    }
  }
}
