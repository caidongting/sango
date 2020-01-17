package com.caidt.infrastructure

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Address
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import akka.cluster.sharding.ShardRegion
import com.caidt.infrastructure.database.Session
import com.caidt.infrastructure.database.buildSessionFactory
import com.caidt.infrastructure.database.shardIdOf
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.hibernate.SessionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

enum class Role {
  gate,
  home,
  world,
  battle,
  data,
  ;
}

fun getRemoteSeedNodes(): List<Address> {
  return listOf(
    Address("akka.tcp", CLUSTER_NAME, localHost, 2552),
    Address("akka.tcp", CLUSTER_NAME, localHost, 2553)
  )
}

abstract class GameServer(port: Int) {

  abstract val role: Role

  val logger: Logger = LoggerFactory.getLogger(javaClass)

  /** hibernate */
  private val sessionFactory: SessionFactory by lazy { buildSessionFactory() }

  val dao: Session by lazy { Session(sessionFactory) }

  /** actor system */
  val actorSystem: ActorSystem by lazy { ActorSystem.create(CLUSTER_NAME) }

  val cluster: Cluster = Cluster.get(actorSystem)

  /** netty actor session*/
  private val netty: NettyTcpServer = NettyTcpServer(port = 12121)

  /** znode */
  private val znode = Znode()

  lateinit var shardRegion: ActorRef
    private set

  lateinit var homeProxy: ActorRef
    private set

  lateinit var worldProxy: ActorRef
    private set

  abstract fun start()

  abstract fun close()

  fun buildActorSystem() {
    val config: Config = ConfigFactory.load("application.conf")
    val system = ActorSystem.create(CLUSTER_NAME, config)
  }

  fun startSystem() {
//    cluster.join(cluster.selfMember().address())
    val seedNodes = getSeedNodes()
    cluster.joinSeedNodes(seedNodes)
//    cluster.join(cluster.selfAddress())
    cluster.registerOnMemberUp {
      logger.info("cluster is Up!")
    }
  }

  fun closeSystem() {
//    cluster.leave(cluster.selfAddress())
  }

  fun beforeInit() {
//    znode.start()
  }

  private fun getSeedNodes(): List<Address> {
    val list = getRemoteSeedNodes().toMutableList()
    if (list.first() != cluster.selfAddress()) {
      list.remove(cluster.selfAddress())
    }
    return list
  }

  fun afterClose() {
//    znode.close()
  }

  fun startNetwork() {
    netty.init()
  }

  fun startShardRegion(entity: Class<*>) {
    val settings = ClusterShardingSettings.create(actorSystem).withRole(role.name)
    shardRegion = ClusterSharding.get(actorSystem)
      .start(javaClass.typeName, Props.create(entity), settings, messageExtractor)
    logger.info("${role.name} cluster sharding started!")
  }

  fun closeShardRegion() {
    if (this::shardRegion.isInitialized) {
      shardRegion.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
    }
  }

  fun startHomeProxy() {
    homeProxy = ClusterSharding.get(actorSystem)
      .startProxy("homeProxy", Optional.of(Role.home.name), messageExtractor)
  }

  fun closeHomeProxy() {
    homeProxy.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }

  fun startWorldProxy() {
    worldProxy =
      ClusterSharding.get(actorSystem).startProxy("worldProxy", Optional.of(Role.world.name), messageExtractor)
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