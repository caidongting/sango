package com.caidt.message

import com.caidt.*
import com.caidt.proto.ProtoCsMessage.CsMessage
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.GETALLACTIVITYREQUEST
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.LOGINREQUEST


/** 处理客户端消息 */
internal val clientMessageHandlers: Map<CmdCase, (player: PlayerActor, req: CsMessage) -> Unit> = mapOf(
  LOGINREQUEST to { player, req -> playerService.login(player, req.loginRequest) },
  GETALLACTIVITYREQUEST to { player, req -> activityService.activityList(player, req.getAllActivityRequest) }
)
