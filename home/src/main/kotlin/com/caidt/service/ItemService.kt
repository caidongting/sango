package com.caidt.service

import com.caidt.PlayerActor
import com.caidt.infrastructure.AllOpen
import com.caidt.share.HasEnoughItem

@AllOpen
class ItemService {

  fun hasEnough(player: PlayerActor, req: HasEnoughItem) {

    player.patternCS<Unit>()
  }

}
