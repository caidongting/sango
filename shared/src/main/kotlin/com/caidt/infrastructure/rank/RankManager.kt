package com.caidt.infrastructure.rank

import java.time.Instant
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1


class RankManager(val maxRank: Int) {


}

data class Rank(
    val rank: Int,
    val type: Int,
    var data: RankData,
    var lastUpdateTime: Instant
) {
  val playerRank: PlayerRank get() = data as PlayerRank
  val countryRank: CountryRank get() = data as CountryRank

  val isPlayerRank: Boolean get() = data is PlayerRank
  val isCountryRank: Boolean get() = data is CountryRank
}

interface RankData {
  val id: Long
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
    PlayerRank::kill to compareBy { it.kill }
)