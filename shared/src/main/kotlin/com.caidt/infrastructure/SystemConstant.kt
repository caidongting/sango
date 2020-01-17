package com.caidt.infrastructure

import java.net.InetAddress

/**
 * this file contain lots of system important const properties which influence many module of this application,
 * be careful when modifies (add, remove or update etc.) this file.
 */


/** 虚拟结点数 */
const val VIRTUAL_NUM = 100
/** 物理结点数 */
const val PHYSICAL_NUM = 10

/** hibernate config file */
const val HIBERNATE_CFG_FILE = "hibernate.cfg.xml"

const val LARGE_MAILBOX = "akka.actor.large-unbounded-mailbox"
const val SMALL_MAILBOX = "akka.actor.small-unbounded-mailbox"

/** 集群名称，各节点相同 */
const val CLUSTER_NAME = "cluster"

val localHost: String = InetAddress.getLocalHost().hostAddress
