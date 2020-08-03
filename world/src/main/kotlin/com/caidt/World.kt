package com.caidt

import akka.actor.Props
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object World : GameServer(port = 2553) {

  override val role: Role = Role.world

  override fun start() {
    startSystem()
    startShardRegion()
    startWorldProxy()
    startHomeProxy()
  }

  private fun startShardRegion() {
    startShardRegion(Props.create(WorldActor::class.java))
  }

  override fun close() {
    closeHomeProxy()
    closeWorldProxy()
    closeSystem()
  }
}

fun main() {
  World.start()
  // Thread.sleep(10000L)
  // World.homeProxy.tellNoSender(PlayerEnvelope(1L, Ok))
}
