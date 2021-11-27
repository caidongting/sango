package com.caidt.message

import com.caidt.PlayerActor
import com.caidt.itemService
import com.caidt.resourceService
import com.caidt.rewardPackageService
import com.caidt.share.HasEnoughReq
import com.caidt.share.HasEnoughItemReq
import com.caidt.share.HasEnoughResourceReq
import com.caidt.share.PlayerMessage

/** 处理来自内部集群的消息 */
internal val innerMessageHandlers: (PlayerActor, PlayerMessage) -> Unit = { player, req ->
  when (req) {
    is HasEnoughItemReq -> itemService.hasEnough(player, req)
    is HasEnoughResourceReq -> resourceService.hasEnough(player, req)
    is HasEnoughReq -> rewardPackageService.hasEnough(player, req)
  }
}
