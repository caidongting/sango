package com.caidt.share

import com.caidt.proto.ProtoCommon.*
import java.io.Serializable

/** 用于启动actor */
object UP

/** 用于服务器内部定时 */
object Tick

/** 服务器之间异步简单应答 */
object Ok

/** 断开与客户端的连接 */
object Disconnect

object GenerateUid

/** 玩家消息 */
data class PlayerEnvelope(val playerId: Long, val payload: Any)

/** 世界消息 */
data class WorldEnvelope(val worldId: Long, val payload: Any)


interface InnerMessage : Serializable

interface PlayerMessage : InnerMessage {
  val playerId: Long
}

interface WorldMessage : InnerMessage {
  val worldId: Long
}

/** 是否有足够的资源和道具 */
class HasEnough(override val playerId: Long, val reward: DisplayRewardPackage) : PlayerMessage
/** 是否有足够的资源 */
class HasEnoughResource(override val playerId: Long, val resources: List<DisplayResource>) : PlayerMessage
/** 是否有足够的道具 */
class HasEnoughItem(override val playerId: Long, val items: List<DisplayItem>) : PlayerMessage

