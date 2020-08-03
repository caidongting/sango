package com.caidt.share

import java.time.Instant
import java.util.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

data class RankHolder(
  val rank: Int,
  val type: Int,
  var data: RankData,
  var lastUpdateTime: Instant
)

/**
 * 通用排行榜 大 -> 小
 */
abstract class AbstractRankManager(private val maxRank: Int) {

  init {
    require(maxRank >= 0) { "invalid max rank limit $maxRank" }
  }

  private val rankMap: MutableMap<Int, LinkedList<RankHolder>> = hashMapOf()

  abstract fun newRankData(type: Int, uid: Long): RankData

  abstract fun <T> comparator(type: Int, property: KProperty1<RankData, T>): Comparator<RankData>


  fun findRanks(type: Int): List<RankHolder> {
    return rankMap[type] ?: emptyList()
  }

  fun <T> updateRank(type: Int, uid: Long, property: KMutableProperty1<RankData, T>, newRankValue: T) {
    val ranks = rankMap[type] ?: LinkedList()
    val comparator = comparator(type, property)

    var newRank = ranks.size + 1 // 新的排名
    val holder: RankHolder? = ranks.find { it.data.id == uid }
    if (holder != null) {
      newRank = holder.rank
      property.set(holder.data, newRankValue)
    } else {
      val newRankData = newRankData(type, uid)
      if (maxRank != 0 && newRank > maxRank) {
        if (comparator.compare(ranks.last.data, newRankData) >= 0) { // 不上榜
          return
        }
      }
      // 新上榜
      ranks.add(RankHolder(newRank, type, newRankData, Instant.now()))
      rankMap[type] = ranks // 保存
    }
    // todo: 更新其他的数据

    // 排序处理
    if (newRank == 1) return // 只有一个元素无需排序
    for (rank in newRank - 1 downTo 1) {
      val preRank = ranks[rank - 1]
      val targetRank = ranks[rank]
      if (comparator.compare(preRank.data, targetRank.data) >= 0) { // 交换
        break
      }
      val preRankData: RankData = preRank.data
      preRank.data = targetRank.data
      targetRank.data = preRankData
    }

    // 将超出排名的删掉
    if (maxRank != 0 && ranks.size > maxRank) {
      ranks.removeLast()
    }
  }

}

