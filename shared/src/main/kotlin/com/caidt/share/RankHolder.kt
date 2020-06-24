package com.caidt.share

import java.time.Instant
import java.util.*

data class RankHolder(
  val rank: Int,
  val type: Int,
  var data: RankData,
  var lastUpdateTime: Instant
)

//fun updateRank(
//  ranks: LinkedList<RankHolder>,
//  rankType: Int,
//  maxRank: Int,
//  uid: Long,
//  newRankValue: Long
//) {
//  val optional: Optional<RankHolder> = ranks.stream().filter { it.data.id == uid }.findFirst()
//  val rankHolder: RankHolder
//  if (optional.isPresent) {
//    rankHolder = optional.get()
//    rankHolder.setRankValue(newRankValue)
//  } else {
//    val size = ranks.size
//    if (maxRank != 0 && size >= maxRank) {
//      val last: RankHolder = ranks.last
//      if (last.getRankValue() >= newRankValue) { // 不上榜
//        return
//      }
//    }
//    // 新上榜
//    rankHolder = RankHolder(uid, rankType, newRankValue, TimeHelper.getCurrentSecond())
//    rankHolder.setRank(size + 1)
//    ranks.add(rankHolder)
//  }
//  for (rank in rankHolder.getRank() - 1 downTo 1) {
//    val preRank: RankHolder = ranks[rank - 1]
//    if (preRank.getRankValue() >= rankHolder.getRankValue()) {
//      break
//    }
//    preRank.setRank(rankHolder.getRank())
//    rankHolder.setRank(rank)
//    ranks[rankHolder.getRank() - 1] = rankHolder
//    ranks[preRank.getRank() - 1] = preRank
//  }
//
//  // 将超出排名的删掉
//  while (maxRank != 0 && ranks.size > maxRank) {
//    ranks.removeLast()
//  }
//}