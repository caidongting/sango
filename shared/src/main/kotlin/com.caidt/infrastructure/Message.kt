package com.caidt.infrastructure

/** 用于服务器内部定时 */
object Tick

/** 服务器之间异步简单应答 */
object Ok

/** 客户端通用返回消息 */
object OkResponse

/** 玩家消息 */
open class PlayerMessage {
  /** 目标 */
  open var playerId: PlayerId = 0
}

/** 世界消息 */
open class WorldMessage {
  var worldId: WorldId = 0
}

/** player to player, need answer */
open class PPMessage {
  var from: PlayerId = 0
  var to: PlayerId = 0
}
