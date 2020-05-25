package com.caidt

import akka.actor.UntypedAbstractActor
import com.caidt.proto.ProtoCsMessage
import com.caidt.proto.ProtoDescriptor
import com.caidt.proto.ProtoScMessage
import com.caidt.share.Disconnect
import com.caidt.share.PlayerEnvelope
import com.caidt.share.WorldEnvelope
import com.google.protobuf.MessageLite
import io.netty.channel.ChannelHandlerContext

// 负责与player通信
class Session(private val ctx: ChannelHandlerContext) : UntypedAbstractActor() {

  override fun onReceive(message: Any?) {
    when (message) {
      Disconnect -> ctx.disconnect()
//      is ProtoCsMessage.CsMessage -> handleCsMessage(playerId, message)
      is ProtoDescriptor.Request -> handleClientMessage(msg = message)
      is ProtoScMessage.ScMessage -> handleScMessage(message)
      is ByteArray -> ctx.write(message) // 大文件直接传输字节流
      is MessageLite -> {
      } // 默认的
      else -> unhandled(message)
    }
  }

  private fun handleClientMessage(msg: ProtoDescriptor.Request) {
    if (!validate(msg)) return

    val csMessage = ProtoCsMessage.CsMessage.parseFrom(msg.req)
    handleCsMessage(msg.uid, csMessage)
  }

  private fun handleCsMessage(uid: Long, csMessage: ProtoCsMessage.CsMessage) {
    when (csMessage.cmdCase) {
      in csMessageToWorld -> sendToWorld(uid, csMessage)
      in csMessageToHome -> sendToHome(uid, csMessage)
      else -> sendToHome(uid, csMessage)
    }
  }

  /** 验证消息合法性 */
  private fun validate(message: ProtoDescriptor.Request): Boolean {
    val uid = message.uid
    val token = message.token

    // 验证账号信息使否合法
    // 需要获取PlayerEntity信息，比对账号密码等信息
    return true
  }

  private fun sendToHome(playerId: Long, msg: MessageLite) {
    val envelope = PlayerEnvelope(playerId, msg)
    Gate.homeProxy.tell(envelope, self)
  }

  private fun sendToWorld(worldId: Long, msg: MessageLite) {
    val envelope = WorldEnvelope(worldId, msg)
    Gate.worldProxy.tell(envelope, self)
  }

  private fun handleMessageList(msg: MessageLite) {
    ProtoScMessage.ScMessage.newBuilder().apply {
//      setField(msg)
    }
  }

  private fun handleScMessage(scMessage: ProtoScMessage.ScMessage) {
    val builder = ProtoDescriptor.Response.newBuilder().apply {
      resp = scMessage.toByteString()
    }
    ctx.writeAndFlush(builder.build())
  }

  private fun handleServerMessage(msg: ProtoDescriptor.Response) {
    // 看是否有监听的，若监听则分发
    ctx.writeAndFlush(msg)
  }


}
