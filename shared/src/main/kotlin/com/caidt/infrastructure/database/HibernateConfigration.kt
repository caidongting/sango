package com.caidt.infrastructure.database

import com.caidt.infrastructure.*
import com.caidt.share.Reason
import com.caidt.share.entity.PlayerEntity
import com.caidt.share.entity.WorldEntity
import org.hibernate.cfg.Configuration
import org.hibernate.shards.ShardId
import org.hibernate.shards.ShardedConfiguration
import org.hibernate.shards.cfg.ConfigurationToShardConfigurationAdapter
import org.hibernate.shards.cfg.ShardConfiguration
import org.hibernate.shards.cfg.ShardedEnvironment.SHARD_ID_PROPERTY
import org.hibernate.shards.session.ShardedSessionFactory
import org.hibernate.shards.strategy.ShardStrategyFactory
import org.hibernate.shards.strategy.ShardStrategyImpl
import org.hibernate.shards.strategy.access.SequentialShardAccessStrategy
import org.hibernate.shards.strategy.resolution.AllShardsShardResolutionStrategy
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy
import java.util.*

fun shardIdOf(entity: PlayerEntity): ShardId = shardIdOf(entity.playerId)

fun shardIdOf(entity: WorldEntity): ShardId = shardIdOf(entity.worldId)

fun shardIdOf(uid: Long): ShardId = ShardId((uid % VIRTUAL_NUM).toInt())

fun buildSessionFactory(): ShardedSessionFactory {
  val configFile = HIBERNATE_CFG_FILE
  val prototypeConfig = Configuration().configure(configFile)
  val shardConfigs: List<ShardConfiguration> = buildShardConfigs(configFile)
  val strategyFactory: ShardStrategyFactory = buildShardStrategyFactory()
  val shardedConfig = ShardedConfiguration(
    prototypeConfig,
    shardConfigs,
    strategyFactory,
    virtualMap
  )
  return shardedConfig.buildShardedSessionFactory()
}

internal fun buildShardStrategyFactory(): ShardStrategyFactory {
  return ShardStrategyFactory { shardIds ->
    val selectionStrategy = ShardSelectionStrategy { obj ->
      when (obj) {
        is PlayerEntity -> shardIdOf(obj)
        is WorldEntity -> shardIdOf(obj)
        else -> throw ServerException(Reason.SYSTEM, "no shardId")
      }
    }
    val resolutionStrategy = AllShardsShardResolutionStrategy(shardIds)
    val accessStrategy = SequentialShardAccessStrategy()
    ShardStrategyImpl(selectionStrategy, resolutionStrategy, accessStrategy)
  }
}

internal fun buildShardConfigs(configFile: String): List<ShardConfiguration> {
  return (0 until PHYSICAL_NUM).map {
    val config = Configuration().configure(configFile)
    val properties = Properties()
    properties.setProperty(SHARD_ID_PROPERTY, "$it")
    config.addProperties(properties)
    ConfigurationToShardConfigurationAdapter(config)
  }
}

/** 虚拟结点映射到物理结点 */
internal val virtualMap: Map<Int, Int> = mapOf(
  0 to 0,
  1 to 0,
  2 to 0,
  3 to 0,
  4 to 0,
  5 to 0,
  6 to 0,
  7 to 0,
  8 to 0,
  9 to 0,
  10 to 1,
  11 to 1,
  12 to 1,
  13 to 1,
  14 to 1,
  15 to 1,
  16 to 1,
  17 to 1,
  18 to 1,
  19 to 1,
  20 to 2,
  21 to 2,
  22 to 2,
  23 to 2,
  24 to 2,
  25 to 2,
  26 to 2,
  27 to 2,
  28 to 2,
  29 to 2,
  30 to 3,
  31 to 3,
  32 to 3,
  33 to 3,
  34 to 3,
  35 to 3,
  36 to 3,
  37 to 3,
  38 to 3,
  39 to 3,
  40 to 4,
  41 to 4,
  42 to 4,
  43 to 4,
  44 to 4,
  45 to 4,
  46 to 4,
  47 to 4,
  48 to 4,
  49 to 4,
  50 to 5,
  51 to 5,
  52 to 5,
  53 to 5,
  54 to 5,
  55 to 5,
  56 to 5,
  57 to 5,
  58 to 5,
  59 to 5,
  60 to 6,
  61 to 6,
  62 to 6,
  63 to 6,
  64 to 6,
  65 to 6,
  66 to 6,
  67 to 6,
  68 to 6,
  69 to 6,
  70 to 7,
  71 to 7,
  72 to 7,
  73 to 7,
  74 to 7,
  75 to 7,
  76 to 7,
  77 to 7,
  78 to 7,
  79 to 7,
  80 to 8,
  81 to 8,
  82 to 8,
  83 to 8,
  84 to 8,
  85 to 8,
  86 to 8,
  87 to 8,
  88 to 8,
  89 to 8,
  90 to 9,
  91 to 9,
  92 to 9,
  93 to 9,
  94 to 9,
  95 to 9,
  96 to 9,
  97 to 9,
  98 to 9,
  99 to 9
)




