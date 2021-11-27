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

class SingleRank(private val maxRank: Int) {

  private val ranks: LinkedList<RankHolder> = LinkedList()
  private val rankMap: MutableMap<Long, Int> = hashMapOf()

  fun allRank(): List<RankHolder> = ranks

  fun <T> update(uid: Long, property: KMutableProperty1<RankData, T>, newRankValue: T) {
    val rank = rankMap[uid] ?: (ranks.size + 1)
    val holder = ranks[rank - 1]
    property.set(holder.data, newRankValue) // 更新数据

    val iterator = ranks.listIterator(holder.rank)
    while (iterator.hasPrevious()) {
      val previous = iterator.previous()


      swapData(previous, holder)
    }

    // remove last
    if (ranks.size > maxRank) {
      ranks.removeLast().also { rankMap.remove(it.data.id) }
    }
  }

  private fun swapData(a: RankHolder, b: RankHolder) {
    val rankData = a.data
    a.data = b.data
    b.data = rankData
  }

}

/**
 * 通用排行榜 大 -> 小
 */
abstract class AbstractRankManager(private val maxRank: Int) {

  init {
    require(maxRank >= 0) { "invalid max rank limit $maxRank" }
  }

  private val rankMap: MutableMap<Int, LinkedList<RankHolder>> = hashMapOf()

  abstract fun load()

  abstract fun newRankData(type: Int, uid: Long): RankData

  abstract fun <T> comparator(type: Int, property: KProperty1<RankData, T>): Comparator<RankData>


  fun findRanks(type: Int): List<RankHolder> {
    return rankMap[type] ?: emptyList()
  }

  fun <T> updateRank(type: Int, uid: Long, property: KMutableProperty1<RankData, T>, newRankValue: T) {
    val ranks = rankMap[type] ?: LinkedList()
    val comparator = comparator(type, property)

    var newRank = ranks.size + 1 // 新的排名
    var holder: RankHolder? = ranks.find { it.data.id == uid } // todo: 优化
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
      holder = RankHolder(newRank, type, newRankData, Instant.now())
      ranks.add(holder)
      rankMap[type] = ranks // 保存
    }
    // todo: 更新其他的数据

    // 排序处理
    if (newRank == 1) return // 只有一个元素无需排序
    var cur: RankHolder = holder
    val iterator = ranks.listIterator(newRank - 1)
    while (iterator.hasPrevious()) {
      val preRank = iterator.previous()
      if (comparator.compare(preRank.data, cur.data) >= 0) { // 交换
        break
      }
      swapData(preRank, cur)
      cur = preRank
    }

    // 将超出排名的删掉
    if (maxRank != 0 && ranks.size > maxRank) {
      ranks.removeLast()
    }
  }

  fun swapData(a: RankHolder, b: RankHolder) {
    val rankData = a.data
    a.data = b.data
    b.data = rankData
  }

}

