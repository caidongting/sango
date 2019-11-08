package com.caidt

import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import akka.cluster.singleton.ClusterSingletonManagerSettings
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.messageExtractor


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  override fun init() {
    startNetwork()

    val settings = ClusterShardingSettings.create(actorSystem)
    val startedCounterRegion = ClusterSharding.get(actorSystem)
        .start(PlayerActor::class.simpleName, Props.create(::PlayerActor), settings, messageExtractor)
  }

  override fun close() {
  }

  fun startSingleton() {
    val settings = ClusterSingletonManagerSettings.create(actorSystem).withRole(role.name)
  }
}
