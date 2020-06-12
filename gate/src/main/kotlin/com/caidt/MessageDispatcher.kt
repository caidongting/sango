package com.caidt

import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.*

/** 分发到home的消息 maybe default option no need list all message */
internal val csMessageToHome: Set<CmdCase> = setOf(
  LOGINREQUEST,
  GETACTIVITYREQUEST,
  GETALLACTIVITYREQUEST
//  GETPLAYERINFOREQUEST
)

/** 分发到world的消息 need list */
internal val csMessageToWorld: Set<CmdCase> = setOf()