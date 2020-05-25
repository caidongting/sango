package com.caidt

import com.caidt.share.Ok
import com.caidt.share.PlayerEnvelope


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}

/** 向其他玩家发送消息 */
fun PlayerActor.sendToPlayer(playerId: Long, msg: Any) {
  val envelope = PlayerEnvelope(playerId, msg)
  Home.homeProxy.tell(envelope, self)
}