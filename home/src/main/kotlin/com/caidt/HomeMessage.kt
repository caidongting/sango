package com.caidt

import com.caidt.infrastructure.PlayerId
import com.caidt.infrastructure.PlayerMessage


fun <T : PlayerMessage> wrapPlayerMessage(playerActor: PlayerActor, msg: T): T {
  return wrapPlayerMessage(playerActor.playerId, msg)
}

fun <T : PlayerMessage> wrapPlayerMessage(playerId: PlayerId, msg: T): T {
  return msg.also { it.playerId = playerId }
}

fun PlayerActor.sendMessage(msg: PlayerMessage) {
  wrapPlayerMessage(this, msg)
}

object UP : PlayerMessage()

class Disconnect : PlayerMessage()