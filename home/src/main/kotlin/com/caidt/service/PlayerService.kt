package com.caidt.service

import com.caidt.Home
import com.caidt.PlayerActor
import com.caidt.infrastructure.AllOpen
import com.caidt.proto.ProtoDescriptor
import java.time.Instant

@AllOpen
class PlayerService {

  fun login(player: PlayerActor, req: ProtoDescriptor.LoginRequest) {

    Home.eventBus.fireLogin(player)
  }


  fun reLogin() {

  }

  /** 计算资源收获 */
  fun calcResource(player: PlayerActor, now: Instant) {
    var a = 1
    var b = 2
    a = b.also { b = a }
  }

}