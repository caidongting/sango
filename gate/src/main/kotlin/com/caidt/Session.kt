package com.caidt

import akka.actor.UntypedAbstractActor
import com.caidt.proto.ProtoCsMessage
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.LOGINREQUEST
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
      // msg from client
      is ProtoDescriptor.Request -> handleClientMessage(message)
      // msg from server
      Disconnect -> disconnect()
      is ProtoScMessage.ScMessage -> handleScMessage(message)
      is ByteArray -> ctx.write(message) // 大文件直接传输字节流
      is MessageLite -> handleMessageLite(message) // 默认的
      else -> unhandled(message)
    }
  }

  private fun disconnect() {
    ctx.disconnect()
  }

  private fun handleClientMessage(msg: ProtoDescriptor.Request) {
    if (!validate(msg)) return

    val csMessage = ProtoCsMessage.CsMessage.parseFrom(msg.req)
    handleCsMessage(msg.uid, csMessage)
  }

  /** 验证消息合法性 */
  private fun validate(message: ProtoDescriptor.Request): Boolean {
    val uid = message.uid
    val token = message.token

    // 验证账号信息使否合法
    // 需要获取PlayerEntity信息，比对账号密码等信息
    return true
  }

  private fun handleCsMessage(uid: Long, csMessage: ProtoCsMessage.CsMessage) {
    when (csMessage.cmdCase) {
      LOGINREQUEST -> login(csMessage.loginRequest)
      in csMessageToWorld -> sendToWorld(uid, csMessage)
      in csMessageToHome -> sendToHome(uid, csMessage)
      else -> sendToHome(uid, csMessage)
    }
  }


  private fun sendToHome(playerId: Long, msg: MessageLite) {
    val envelope = PlayerEnvelope(playerId, msg)
    Gate.homeProxy.tell(envelope, self)
  }

  private fun sendToWorld(worldId: Long, msg: MessageLite) {
    val envelope = WorldEnvelope(worldId, msg)
    Gate.worldProxy.tell(envelope, self)
  }

  private fun handleMessageLite(msg: MessageLite) {
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

  /** 验证玩家账户信息 */
  private fun login(msg: ProtoDescriptor.LoginRequest) {

  }


}
