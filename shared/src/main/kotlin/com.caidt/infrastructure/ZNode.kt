package com.caidt.infrastructure

import akka.actor.Address
import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.ObjectMapper
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
    curatorFramework = CuratorFrameworkFactory.builder()
      .connectString(zooKeeperPath)
      .sessionTimeoutMs(4000)
      .retryPolicy(ExponentialBackoffRetry(1000, 3))
      .build()
    curatorFramework.start()
    logger.debug("Znode stated!")

   curatorFramework.create()
     .creatingParentsIfNeeded()
     .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
     .forPath("/$CLUSTER_NAME")
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

  fun getSeedNodes(): List<Address> {
    val user = System.getProperty("user")
    val bytes = curatorFramework.data.forPath("/$user/seedNodes")
    val list = ObjectMapper().readValue(bytes, ArrayList::class.java)
    return JSON.parseObject(bytes, List::class.java)
  }

  fun close() {
    curatorFramework.close()
    logger.debug("Znode closed!")
  }
}