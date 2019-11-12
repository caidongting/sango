package com.caidt

import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import akka.cluster.sharding.ShardRegion
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role
import com.caidt.infrastructure.messageExtractor


object Home : GameServer(port = 2552) {

  override val role: Role = Role.home

  val eventBus: EventBus = EventBus(actorSystem)

  lateinit var shardRegion: ActorRef
    private set

  override fun init() {
    startNetwork()

    startShardRegion()
    startWorldProxy()
  }

  override fun close() {
    closeWorldProxy()
    closeShardRegion()
  }

  fun startShardRegion() {
    val settings = ClusterShardingSettings.create(actorSystem).withRole(role.name)
    shardRegion = ClusterSharding.get(actorSystem)
        .start(javaClass.name, Props.create(::PlayerActor), settings, messageExtractor)
  }

  fun closeShardRegion() {
    shardRegion.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }
}
