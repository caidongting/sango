package com.caidt

import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.GETACTIVITYREQUEST
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.GETALLACTIVITYREQUEST

/** 分发到world的消息 need list */
internal val csMessageToWorld: Set<CmdCase> = setOf(
  GETACTIVITYREQUEST,
  GETALLACTIVITYREQUEST
)