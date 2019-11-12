package com.caidt.infrastructure

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.`ShardingEnvelope$`
import akka.cluster.sharding.typed.internal.protobuf.ShardingMessages
import com.caidt.infrastructure.database.Session
import com.caidt.infrastructure.database.buildSessionFactory
import com.caidt.infrastructure.database.shardIdOf
import org.hibernate.SessionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

enum class Role {
  gate,
  home,
  world,
  data,
  ;
}

abstract class GameServer(port: Int) {

  abstract val role: Role

  val logger: Logger = LoggerFactory.getLogger(javaClass)

  /** hibernate */
  private val sessionFactory: SessionFactory by lazy { buildSessionFactory() }

  val dao: Session by lazy { Session(sessionFactory) }

  /** actor system */
  lateinit var actorSystem: ActorSystem
    private set

  /** netty actor session*/
  private val netty: NettyTcpServer = NettyTcpServer(port = port)

  lateinit var homeProxy: ActorRef
    private set

  lateinit var worldProxy: ActorRef
    private set

  abstract fun init()

  abstract fun close()

  fun startActorSystem() {
    actorSystem = ActorSystem.create()
  }

  fun startNetwork() {
    netty.init()
  }

  fun startHomeProxy() {
    homeProxy = ClusterSharding.get(actorSystem).startProxy("homeProxy", Optional.of(Role.home.name), messageExtractor)
  }

  fun closeHomeProxy() {
    homeProxy.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }

  fun startWorldProxy() {
    worldProxy = ClusterSharding.get(actorSystem).startProxy("worldProxy", Optional.of(Role.world.name), messageExtractor)
  }

  fun closeWorldProxy() {
    worldProxy.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }

}

val messageExtractor: ShardRegion.MessageExtractor = object : ShardRegion.MessageExtractor {
  override fun entityId(message: Any): String {
    return when (message) {
      is PlayerEnvelope -> message.playerId.toString()
      is WorldEnvelope -> message.worldId.toString()
      else -> message.toString()
    }
  }

  override fun entityMessage(message: Any): Any {
    return when (message) {
      is PlayerEnvelope -> message.payload
      is WorldEnvelope -> message.payload
      else -> message.toString()
    }
  }

  override fun shardId(message: Any): String {
    return when (message) {
      is PlayerEnvelope -> shardIdOf(message).toString()
      is WorldEnvelope -> shardIdOf(message).toString()
      else -> message.toString()
    }
  }

}