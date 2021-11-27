package com.caidt

import akka.actor.Props
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus by lazy { EventBus(actorSystem) }

  val commonTick: CommonTick by lazy { CommonTick(actorSystem) }

  override fun preStart() {
    loadExcelConfig()
    startShardRegion()
    startHomeProxy()
    startWorldProxy()
  }

  override fun postStop() {
    closeWorldProxy()
    closeHomeProxy()
    closeShardRegion()
  }

  private fun startShardRegion() {
    startShardRegion(Props.create(PlayerActor::class.java))
  }
}


fun main(args: Array<String>) {
  Home.start()
}