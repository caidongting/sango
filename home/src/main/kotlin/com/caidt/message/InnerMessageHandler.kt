package com.caidt.message

import com.caidt.PlayerActor
import com.caidt.itemService
import com.caidt.resourceService
import com.caidt.rewardPackageService
import com.caidt.share.HasEnough
import com.caidt.share.HasEnoughItem
import com.caidt.share.HasEnoughResource
import com.caidt.share.PlayerMessage

/** 处理来自内部集群的消息 */
internal val innerMessageHandlers: (PlayerActor, PlayerMessage) -> Unit = { player, req ->
  when (req) {
    is HasEnoughItem -> itemService.hasEnough(player, req)
    is HasEnoughResource -> resourceService.hasEnough(player, req)
    is HasEnough -> rewardPackageService.hasEnough(player, req)
  }
}
