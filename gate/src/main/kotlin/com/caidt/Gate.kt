package com.caidt

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.caidt.infrastructure.Role

object Gate {

  val port: Int = 2551

  val role: Role = Role.gate

  val actorSystem = ActorSystem.create()

  lateinit var homeProxy: ActorRef
    private set

  lateinit var worldProxy: ActorRef
    private set

  /** netty actor session*/
  private val clientNetty: NettyTcpServer = NettyTcpServer(port = 12121)


  fun start() {
    clientNetty.start()
  }

}