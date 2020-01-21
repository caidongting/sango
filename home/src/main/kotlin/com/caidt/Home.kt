package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus = EventBus(actorSystem)

  override fun start() {
    startSystem()
    startShardRegion()
    startNetwork()
    startWorldProxy()
  }

  override fun close() {
    closeWorldProxy()
    closeShardRegion()
    closeSystem()
  }

  fun startShardRegion() {
    startShardRegion(PlayerActor::class.java)
  }
}


fun main() {
  Home.start()
}