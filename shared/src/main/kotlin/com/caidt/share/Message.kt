package com.caidt.share

/** 用于服务器内部定时 */
object Tick

/** 服务器之间异步简单应答 */
object Ok

/** 玩家消息 */
data class PlayerEnvelope(val playerId: Long, val payload: Any)

/** 世界消息 */
data class WorldEnvelope(val worldId: Long, val payload: WorldMessage)


open class PlayerMessage

open class WorldMessage

/** player to player, need answer */
open class PPMessage : PlayerMessage() {
  var from: Long = 0
  var to: Long = 0
}
