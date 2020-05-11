package com.caidt.infrastructure

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

const val zooKeeperPath = "192.168.199.240:2181,192.168.199.240:2182,192.168.199.240:2183"

fun initZookeeper() {
  val curatorFramework = CuratorFrameworkFactory.builder()
    .connectString(zooKeeperPath)
    .sessionTimeoutMs(4000)
    .retryPolicy(ExponentialBackoffRetry(1000, 3))
    .build()
  curatorFramework.start()


  // 1. 创建节点
  val node = "/${CLUSTER_NAME}"
  val data = curatorFramework.checkExists().forPath(node)
  if (data == null) {
    curatorFramework.create()
      .creatingParentsIfNeeded()
      .withMode(CreateMode.PERSISTENT)
      .forPath(node, "0".toByteArray())
  }

  // 2. 填充seedNodes


  curatorFramework.close()
}