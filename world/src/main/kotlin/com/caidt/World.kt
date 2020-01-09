package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object World : GameServer(port = 2553) {

  override val role: Role = Role.world

  override fun start() {
  }

  override fun close() {
  }
}
