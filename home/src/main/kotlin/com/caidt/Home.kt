package com.caidt

import akka.cluster.singleton.ClusterSingletonManagerSettings
import com.caidt.infrastructure.GameServer
import com.caidt.infrastructure.Role


object Home : GameServer(port = 12121) {

  override val role: Role = Role.home

//  val settings = ClusterSingletonManagerSettings.create(context.system).withRole(role.name)

  override fun init() {
    startNetwork()
  }

  override fun close() {
  }

}
