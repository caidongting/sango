package com.caidt.infrastructure

import com.caidt.util.JSON
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

const val testZookeeperIp = "106.14.65.149"
const val zooKeeperPath = "$testZookeeperIp:2181,$testZookeeperIp:2182,$testZookeeperIp:2183"

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
  val seedNodes = "/seedNodes"
  val exists = curatorFramework.checkExists().forPath(seedNodes)
  if (exists != null) {
    curatorFramework.delete().deletingChildrenIfNeeded().forPath(seedNodes)
  }
  val data = listOf(
    "home:akka.tcp:$localhost:2552",
    "world:akka.tcp:$localhost:2553"
  )

  curatorFramework.create()
    .creatingParentsIfNeeded()
    .withMode(CreateMode.PERSISTENT)
    .forPath(seedNodes, JSON.toByteArray(data))

  // 3.


  curatorFramework.close()
}