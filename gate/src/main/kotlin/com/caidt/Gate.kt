package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.client.ClusterClientReceptionist
import com.caidt.infrastructure.CLUSTER_NAME
import com.caidt.infrastructure.Role
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Gate {

  val port: Int = 2551

  val role: Role = Role.gate

  val actorSystem: ActorSystem by lazy { buildActorSystem() }

  lateinit var homeProxy: ActorRef
    private set

  lateinit var worldProxy: ActorRef
    private set

  /** netty actor session*/
  private val clientNetty: NettyTcpServer = NettyTcpServer(port = 12121)


  private fun buildActorSystem(): ActorSystem {
    val additionConfig: String = """
      akka.remote.netty.tcp.port=$port
    """.trimIndent()
    val config: Config = ConfigFactory.parseString(additionConfig).withFallback(ConfigFactory.load())
    return ActorSystem.create(CLUSTER_NAME, config)
  }

  fun start() {
    clientNetty.start()

    val actorRef = actorSystem.actorOf(UidGenerator.props(), "uidGenerator")
    ClusterClientReceptionist.get(actorSystem).registerService(actorRef);

    val channel = 128L
    ChannelBus.subscribe(actorRef, channel)
    ChannelBus.unsubscribe(actorRef, channel)
  }

}


fun main() {
  Gate.start()
}
