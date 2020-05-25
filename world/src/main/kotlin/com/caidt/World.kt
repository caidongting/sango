package com.caidt

import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.share.Ok
import com.caidt.share.PlayerEnvelope
import com.caidt.share.tellNoSender


object World : GameServer(port = 2553) {

  override val role: Role = Role.world

  override fun start() {
    startSystem()
    startShardRegion()
    startWorldProxy()
    startHomeProxy()
  }

  private fun startShardRegion() {
    startShardRegion(WorldActor::class.java)
  }

  override fun close() {
    closeHomeProxy()
    closeWorldProxy()
    closeSystem()
  }
}

fun main() {
  World.start()
  Thread.sleep(10000L)
  World.homeProxy.tellNoSender(PlayerEnvelope(1L, Ok))
}
