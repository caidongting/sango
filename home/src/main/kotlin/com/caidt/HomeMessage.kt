package com.caidt

import com.caidt.infrastructure.PlayerEnvelope
import com.caidt.infrastructure.PlayerId
import com.caidt.infrastructure.PlayerMessage
import com.google.protobuf.MessageLite


fun wrapEnvelope(playerId: PlayerId, msg: Any): PlayerEnvelope {
  return PlayerEnvelope(playerId, msg)
}

fun PlayerActor.sendMessage(msg: Any) {
  when (msg) {
    is PlayerEnvelope -> {
    }
    is PlayerMessage -> {
    }
    is MessageLite -> {
    }
  }
}

object UP

class Disconnect