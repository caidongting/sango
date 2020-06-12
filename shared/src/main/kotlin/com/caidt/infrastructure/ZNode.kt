package com.caidt.infrastructure

import akka.actor.Address
import com.caidt.util.JSON
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.api.CuratorWatcher
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.WatchedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ZNode {

  private lateinit var curatorFramework: CuratorFramework

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  fun start() {
    val zooKeeperPath = if (isDev) zooKeeperPath else ""
    curatorFramework = CuratorFrameworkFactory.builder()
      .connectString(zooKeeperPath)
      .sessionTimeoutMs(4000)
      .retryPolicy(ExponentialBackoffRetry(1000, 3))
      .build()
    curatorFramework.start()
    logger.debug("Znode stated!")

    curatorFramework.children.usingWatcher(CuratorWatcher { event: WatchedEvent ->
      val state = event.state
    }).forPath("")
//
//    curatorFramework.inTransaction()
//      .create().forPath("")
//      .and().create().forPath("")
//      .and().delete().forPath("")
//      .and().commit()
  }

  /** 服务器上线注册临时节点 */
  fun register(server: GameServer) {
    val path = "/$CLUSTER_NAME/${server.role}"
    val data = "$localhost:${server.port}"
    curatorFramework.create()
      .creatingParentsIfNeeded()
      .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
      .forPath(path, data.toByteArray())
  }

  fun getSeedNodes(): List<Address> {
    val user = System.getProperty("user") ?: DEFAULT_USER
    val bytes = curatorFramework.data.forPath("/$user/seedNodes")
    return JSON.parseArray(bytes.toString(), Address::class.java)
  }

  fun close() {
    curatorFramework.close()
    logger.debug("Znode closed!")
  }
}