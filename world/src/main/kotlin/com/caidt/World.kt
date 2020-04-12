package com.caidt

import akka.actor.ActorRef
import com.caidt.infrastructure.GameServer
import com.caidt.share.Ok
import com.caidt.share.PlayerEnvelope
import com.caidt.infrastructure.Role


object World : GameServer(port = 2553) {

  override val role: Role = Role.world

  override fun start() {
    startSystem()
    startShardRegion()
    startHomeProxy()
    startNetwork()
  }

  private fun startShardRegion() {
    startShardRegion(WorldActor::class.java)
  }

  override fun close() {
    closeHomeProxy()
    closeSystem()
  }
}

fun main() {
  World.start()
  Thread.sleep(10000L)
  World.homeProxy.tell(PlayerEnvelope(1L, Ok), ActorRef.noSender())
}
