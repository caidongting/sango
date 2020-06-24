package com.caidt.message

import com.caidt.*
import com.caidt.share.*

/** 处理来自内部集群的消息 */
internal val innerMessageHandlers: (PlayerActor, PlayerMessage) -> Unit = { player: PlayerActor, req: PlayerMessage ->
  when (req) {
    is HasEnoughItem -> itemService.hasEnough(player, req)
    is HasEnoughResource -> resourceService.hasEnough(player, req)
    is HasEnough -> rewardPackageService.hasEnough(player, req)
  }
}
