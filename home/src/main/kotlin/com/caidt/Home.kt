package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.config.ExcelConfigs


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus = EventBus(actorSystem)

  override fun start() {
    startSystem()
    startShardRegion()
    startNetwork()
    startWorldProxy()

    ExcelConfigs.init()
  }

  override fun close() {
    closeWorldProxy()
    closeSystem()
  }

  private fun startShardRegion() {
    startShardRegion(PlayerActor::class.java)
  }
}


fun main() {
  Home.start()
}