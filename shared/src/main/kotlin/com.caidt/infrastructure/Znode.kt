package com.caidt.infrastructure

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress

val localHost = InetAddress.getLocalHost().hostAddress

const val zooKeeperPath = "192.168.199.240:2181,192.168.199.240:2182,192.168.199.240:2183"

class Znode {

  private lateinit var curatorFramework: CuratorFramework

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  fun start() {
    curatorFramework = CuratorFrameworkFactory.builder()
      .connectString(zooKeeperPath)
      .sessionTimeoutMs(4000)
      .retryPolicy(ExponentialBackoffRetry(1000, 3))
      .build()
    curatorFramework.start()
    logger.debug("Znode stated!")

//    curatorFramework.create()
//      .creatingParentsIfNeeded()
//      .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
//      .forPath("")
//
//    curatorFramework.children.usingWatcher(CuratorWatcher { event: WatchedEvent ->
//      val state = event.state
//    })
//
//    curatorFramework.inTransaction()
//      .create().forPath("")
//      .and().create().forPath("")
//      .and().delete().forPath("")
//      .and().commit()
  }

  fun close() {
    curatorFramework.close()
    logger.debug("Znode closed!")
  }
}