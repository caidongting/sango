package com.caidt.infrastructure

import akka.actor.ActorSystem
import akka.cluster.sharding.ShardRegion
import com.caidt.infrastructure.database.Session
import com.caidt.infrastructure.database.buildSessionFactory
import com.caidt.infrastructure.database.shardIdOf
import org.hibernate.SessionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

enum class Role {
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

  abstract fun init()

  abstract fun close()

  fun startActorSystem() {
    actorSystem = ActorSystem.apply()
  }

  fun startNetwork() {
    netty.init()
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
      else -> throw UnsupportedOperationException(message.toString())
    }
  }

  override fun shardId(message: Any): String {
    return when (message) {
      is PlayerEnvelope -> shardIdOf(message).toString()
      is WorldEnvelope -> shardIdOf(message).toString()
      else -> throw UnsupportedOperationException(message.toString())
    }
  }

}