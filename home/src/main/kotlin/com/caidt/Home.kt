package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object Home : GameServer(port = 12121) {

  override val role: Role = Role.home

  override fun init() {
    startNetwork()
  }

  override fun close() {
  }

}
