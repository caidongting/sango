package com.caidt.message

import com.caidt.PlayerActor
import com.caidt.itemService
import com.caidt.resourceService
import com.caidt.share.HasEnoughItem
import com.caidt.share.HasEnoughResource
import com.caidt.share.PlayerMessage

/** 处理来自内部集群的消息 */
internal val innerMessageHandlers: (PlayerActor, PlayerMessage) -> Unit = { player: PlayerActor, req: PlayerMessage ->
  when (req) {
    is HasEnoughItem -> itemService.hasEnough(player, req) // 获取道具
    is HasEnoughResource -> resourceService.hasEnough(player, req)
  }
}