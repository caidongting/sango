package com.caidt.infrastructure.rank

import java.time.Instant


class RankManager(val maxRank: Int) {



}

data class RankData(
    val rank: Int,
    var lastTime: Instant,
    var data: Any
)
