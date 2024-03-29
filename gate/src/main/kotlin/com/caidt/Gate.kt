package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
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
    startUidGenerator()
  }

  private fun startUidGenerator() {
    val settings = ClusterSingletonManagerSettings.create(actorSystem).withRole(role.name)
    val props = ClusterSingletonManager.props(
        Props.create(UidGenerator::class.java), PoisonPill.getInstance(), settings
    )
    val actorRef = actorSystem.actorOf(props, "uidGenerator")
    ClusterClientReceptionist.get(actorSystem).registerService(actorRef)
  }

  fun testBus() {
    // val channel = 128L
    // Bus.subscribe(actorRef, channel)
    // Bus.unsubscribe(actorRef, channel)
    // Bus.unsubscribeAll(actorRef)
  }

}


fun main(args: Array<String>) {
  Gate.start()
}
