package com.caidt

import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.messageExtractor


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus = EventBus(actorSystem)

  override fun start() {
    startShardRegion()
    startWorldProxy()
    startSystem()
    startNetwork()
  }

  override fun close() {
    closeWorldProxy()
    closeShardRegion()
  }

  fun startShardRegion() {
    val settings = ClusterShardingSettings.create(actorSystem).withRole(role.name)
    shardRegion = ClusterSharding.get(actorSystem)
      .start(javaClass.name, PlayerActor.props(), settings, messageExtractor)
  }
}


fun main() {
  Home.start()
}