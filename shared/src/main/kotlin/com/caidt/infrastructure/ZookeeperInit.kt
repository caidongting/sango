package com.caidt.infrastructure

import com.caidt.util.JSON
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

const val zooKeeperPath = "192.168.125.199:2181,192.168.125.199:2182,192.168.125.199:2183"

fun main(args: Array<String>) {
  initZookeeper(zkroot)
}

fun initZookeeper(zkroot: String) {
  val curatorFramework = CuratorFrameworkFactory.builder()
    .connectString(zooKeeperPath)
    .connectionTimeoutMs(20000)
    .sessionTimeoutMs(15000)
    .retryPolicy(ExponentialBackoffRetry(1000, 3))
    .namespace("$CLUSTER_NAME/$zkroot")
    .build()

  curatorFramework.start()


  // 1. 删除已经存在的节点
  curatorFramework.delete().deletingChildrenIfNeeded().forPath("/seedNodes")

  // 2. 创建新节点
  val data = listOf(
    "home:akka.tcp:$CLUSTER_NAME:$localhost:2552",
    "world:akka.tcp:$CLUSTER_NAME:$localhost:2553"
  )

  curatorFramework.create()
    .creatingParentsIfNeeded()
    .withMode(CreateMode.PERSISTENT)
    .forPath("/seedNodes", JSON.toByteArray(data))

  // builder.forPath("home")
  // builder.forPath("world")
  // builder.forPath("data")
  // builder.forPath("battle")

  // 2. 填充seedNodes


  // curatorFramework.close()
}