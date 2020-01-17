package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus = EventBus(actorSystem)

  override fun start() {
    startSystem()
    startShardRegion()
    startWorldProxy()
    startNetwork()
  }

  override fun close() {
    closeShardRegion()
    closeWorldProxy()
  }

  fun startShardRegion() {
    startShardRegion(PlayerActor::class.java)
  }
}


fun main() {
  Home.start()
}