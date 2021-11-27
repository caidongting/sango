package com.caidt.service

import com.caidt.PlayerActor
import com.caidt.infrastructure.AllOpen
import com.caidt.share.HasEnoughItemReq

@AllOpen
class ItemService {

  fun hasEnough(player: PlayerActor, req: HasEnoughItemReq) {

    player.patternCS<Unit>()

  }

}
