package com.caidt

import com.caidt.share.PlayerEnvelope
import com.caidt.share.PlayerMessage
import com.google.protobuf.MessageLite


fun wrapEnvelope(playerId: Long, msg: Any): PlayerEnvelope {
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