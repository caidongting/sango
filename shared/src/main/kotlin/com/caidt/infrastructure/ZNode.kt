package com.caidt.infrastructure

import akka.actor.Address
import com.caidt.util.JSON
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ZNode {

  private lateinit var curatorFramework: CuratorFramework

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  fun start() {
    val zooKeeperPath = if (isDev) zooKeeperPath else ""
    curatorFramework = CuratorFrameworkFactory.builder()
      .connectString(zooKeeperPath)
      .connectionTimeoutMs(20 * 1000)
      .retryPolicy(ExponentialBackoffRetry(1000, 3))
      .build()
    curatorFramework.start()
    logger.info("Znode stated!")
  }

  /** 服务器上线注册临时节点 */
  fun register(server: GameServer) {
    val prefix = "/$CLUSTER_NAME/$zkroot/server"
    val data = "$localhost:${server.port}"
    curatorFramework.create()
      .creatingParentsIfNeeded()
      .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
      .forPath("$prefix/${server.role}", data.toByteArray())

    // curatorFramework.children.usingWatcher(CuratorWatcher {
    //   println(it.path)
    //   println(it.toString())
    // }).forPath(prefix)
  }

  fun getSeedNodes(): List<Address> {
    val prefix = "/$CLUSTER_NAME/$zkroot/seedNodes"
    val bytes = curatorFramework.data.forPath(prefix)
    val list = JSON.parseArray(bytes, String::class.java)
    return list.map {
      val (_, protocol, sys, host, port) = it.split(":")
      Address(protocol, sys, host, port.toInt())
    }
  }

  fun close() {
    curatorFramework.close()
    logger.info("Znode closed!")
  }
}