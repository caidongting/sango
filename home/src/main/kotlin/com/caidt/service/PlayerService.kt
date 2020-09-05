package com.caidt.service

import com.caidt.PlayerActor
import com.caidt.infrastructure.AllOpen
import com.caidt.proto.ProtoDescriptor
import java.time.Instant

@AllOpen
class PlayerService {

  fun login(player: PlayerActor, req: ProtoDescriptor.LoginRequest) {

  }


  fun reLogin() {

  }

  /** 计算资源收获 */
  fun calcResource(player: PlayerActor, now: Instant) {

  }

}