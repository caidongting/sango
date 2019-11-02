package com.caidt.world

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object World : GameServer(port = 8081) {

  override val role: Role = Role.world

  override fun init() {
  }

  override fun close() {
  }
}
