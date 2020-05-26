package com.caidt

import com.caidt.proto.ProtoCsMessage
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.LOGINREQUEST


internal val csMessageHandlers: Map<CmdCase, (PlayerActor, ProtoCsMessage.CsMessage) -> Unit> = mapOf(
  LOGINREQUEST to { player, req -> playerService.login(player, req.loginRequest) }
)

