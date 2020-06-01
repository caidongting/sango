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

/** 每结点shards数量 */
const val NUMBER_OF_SHARDS = 1000

/** hibernate config file */
const val HIBERNATE_CFG_FILE = "hibernate.cfg.xml"

const val LARGE_MAILBOX = "akka.actor.large-unbounded-mailbox"
const val SMALL_MAILBOX = "akka.actor.small-unbounded-mailbox"

/** 集群名称，各节点相同 */
const val CLUSTER_NAME = "cluster"
/** 默认用户 */
const val DEFAULT_USER = "root"

/** 本机ip 需确定在unix环境下是否适用 */
val localhost: String = InetAddress.getLocalHost().hostAddress
/** 是否开发模式 */
val isDev: Boolean get() = System.getenv("MODE") == "dev"

/** excel config 目录 */
const val EXCEL_CONFIG_DIR = "com.caidt.share.config"

/** 远程请求超时时间 10s */
const val ASK_TIMEOUT = 10_000L