package com.caidt

import akka.actor.ActorSelection
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.config.ExcelConfigs
import com.caidt.share.PlayerMessage
import com.caidt.share.WorldMessage


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val homeSelection: ActorSelection = actorSystem.actorSelection("/user/home")

  val WorldSelection: ActorSelection = actorSystem.actorSelection("/user/world")

  override fun start() {
    startSystem()
    startShardRegion()
    startHomeProxy()
    startWorldProxy()
    ExcelConfigs.init()
  }

  override fun close() {
    closeWorldProxy()
    closeHomeProxy()
    closeSystem()
  }

  private fun startShardRegion() {
    startShardRegion(PlayerActor::class.java)
  }
}


fun main() {
  Home.start()
}