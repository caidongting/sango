package com.caidt

import akka.actor.Props
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.config.ExcelConfigs


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

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
    startShardRegion(Props.create(PlayerActor::class.java))
  }
}


fun main(args: Array<String>) {
  Home.start()
}