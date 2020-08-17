package com.caidt

import kotlin.reflect.KProperty1


class RankManager(val maxRank: Int)

interface RankData {
  val id: Long

  val playerRank: PlayerRank get() = this as PlayerRank
  val countryRank: CountryRank get() = this as CountryRank

  val isPlayerRank: Boolean get() = this is PlayerRank
  val isCountryRank: Boolean get() = this is CountryRank
}

/** 玩家排行数据 */
data class PlayerRank(
  /** playerId */
  val playerId: Long,
  /** 等级 */
  var level: Long = 1,
  /** 战斗力 */
  var fight: Long = 0,
  /** 杀敌数 */
  var kill: Long = 0
) : RankData {
  override val id: Long
    get() = playerId
}

/** 国家排行数据 */
data class CountryRank(
  /** countryId */
  val countryId: Long
) : RankData {
  override val id: Long
    get() = countryId
}

val PLAYER_COMPARATOR: Map<KProperty1<PlayerRank, Long>, Comparator<PlayerRank>> = mapOf(
  PlayerRank::kill to (compareBy { it.kill })
)