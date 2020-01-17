package com.caidt

import akka.actor.ActorRef
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Ok
import com.caidt.infrastructure.PlayerEnvelope
import com.caidt.infrastructure.Role


object World : GameServer(port = 2553) {

  override val role: Role = Role.world

  override fun start() {
    startSystem()
    startShardRegion()
    startHomeProxy()
//    startNetwork()
  }

  fun startShardRegion() {
    startShardRegion(WorldActor::class.java)
  }

  override fun close() {
    closeShardRegion()
    closeHomeProxy()
  }
}

fun main() {
  World.start()
  Thread.sleep(5000L)
  World.homeProxy.tell(PlayerEnvelope(1L, Ok), ActorRef.noSender())
}
