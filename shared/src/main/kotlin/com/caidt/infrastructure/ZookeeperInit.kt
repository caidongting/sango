package com.caidt.infrastructure

import com.caidt.util.JSON
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

const val zooKeeperPath = "192.168.125.199:2181,192.168.125.199:2182,192.168.125.199:2183"

fun main(args: Array<String>) {
  initZookeeper(ZK_ROOT)
}

fun initZookeeper(zkroot: String) {
  val curatorFramework = CuratorFrameworkFactory.builder()
    .connectString(zooKeeperPath)
    .connectionTimeoutMs(20000)
    .sessionTimeoutMs(15000)
    .retryPolicy(ExponentialBackoffRetry(1000, 3))
    .namespace(zkroot)
    .build()

  curatorFramework.start()


  // 1. 更新seedNodes
  curatorFramework.delete().deletingChildrenIfNeeded().forPath("/seedNodes")
  val data = listOf(
    "home:akka.tcp:$localhost:2552",
    "world:akka.tcp:$localhost:2553"
  )

  curatorFramework.create()
    .creatingParentsIfNeeded()
    .withMode(CreateMode.PERSISTENT)
    .forPath("/seedNodes", JSON.toByteArray(data))

  // 2. 配置表

  // 3.


  curatorFramework.close()
}