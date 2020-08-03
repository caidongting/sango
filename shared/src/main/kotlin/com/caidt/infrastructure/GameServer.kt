package com.caidt.infrastructure

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Address
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings
import akka.cluster.sharding.ShardRegion
import com.caidt.infrastructure.database.Session
import com.caidt.infrastructure.database.buildSessionFactory
import com.caidt.share.GenerateUid
import com.caidt.share.PlayerEnvelope
import com.caidt.share.WorldEnvelope
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("EnumEntryName")
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
    Address("akka.tcp", CLUSTER_NAME, localhost, 2552),
    Address("akka.tcp", CLUSTER_NAME, localhost, 2553)
  )
}

abstract class GameServer(val port: Int) {

  abstract val role: Role

  val logger: Logger = LoggerFactory.getLogger(javaClass)

  /** hibernate */
  val dao: Session by lazy { Session(buildSessionFactory()) }

  /** actor system */
  val actorSystem: ActorSystem by lazy { buildActorSystem() }

  lateinit var cluster: Cluster
    private set

  /** znode */
  private val znode = ZNode()

  lateinit var shardRegion: ActorRef
    private set

  lateinit var homeProxy: ActorRef
    private set

  lateinit var worldProxy: ActorRef
    private set

  lateinit var clusterClient: ActorRef
    private set

  abstract fun start()

  abstract fun close()

  private fun buildActorSystem(): ActorSystem {
    val additionConfig: String = """
      akka.remote.netty.tcp.hostname=$localhost
      akka.remote.netty.tcp.port=$port
    """.trimIndent()
    val config: Config = ConfigFactory.parseString(additionConfig).withFallback(ConfigFactory.load())
    return ActorSystem.create(CLUSTER_NAME, config)
  }

  fun startSystem() {
    znode.start()
    znode.register(this)

    actorSystem.whenTerminated.handle { _, _ -> close() }
    val seedNodes = znode.getSeedNodes()
    cluster = Cluster.get(actorSystem)
    cluster.joinSeedNodes(seedNodes)
    cluster.registerOnMemberUp {
      logger.info("cluster is Up!")
    }
  }

  fun closeSystem() {
//    cluster.leave(cluster.selfAddress())
    closeShardRegion()
    znode.close()
  }

  fun startShardRegion(props: Props) {
    val settings = ClusterShardingSettings.create(actorSystem).withRole(role.name)
    shardRegion = ClusterSharding.get(actorSystem)
      .start(javaClass.simpleName, props, settings, messageExtractor)
    // logger.info("cluster shardRegion is started!")
  }

  private fun closeShardRegion() {
//    if (this::shardRegion.isInitialized) {
    shardRegion.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
//    }
  }

  fun startHomeProxy() {
    homeProxy = ClusterSharding.get(actorSystem)
      .startProxy("homeProxy", Optional.of(Role.home.name), messageExtractor)
  }

  fun closeHomeProxy() {
    homeProxy.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }

  fun startWorldProxy() {
    worldProxy = ClusterSharding.get(actorSystem)
      .startProxy("worldProxy", Optional.of(Role.world.name), messageExtractor)
  }

  fun closeWorldProxy() {
    worldProxy.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
  }

  fun startClusterClient() {
    val clientSettings = ClusterClientSettings.create(actorSystem)
    clusterClient = actorSystem.actorOf(ClusterClient.props(clientSettings), "gate")
  }

  fun getUid(actorRef: ActorRef): Long {
    clusterClient.tell(ClusterClient.Send("/user/gate/uidGenerator", GenerateUid), actorRef)
    return 0L
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
      is PlayerEnvelope -> (message.playerId % NUMBER_OF_SHARDS).toString()
      is WorldEnvelope -> (message.worldId % NUMBER_OF_SHARDS).toString()
      else -> message.toString()
    }
  }

}