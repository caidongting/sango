package com.caidt

import akka.actor.UntypedAbstractActor
import com.caidt.proto.*
import com.caidt.proto.ProtoCsMessage.CsMessage.CmdCase.LOGINREQUEST
import com.caidt.share.*
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.MessageLite
import io.netty.channel.ChannelHandlerContext

// 负责与player通信
class ChannelSession(private val ctx: ChannelHandlerContext) : UntypedAbstractActor() {

  override fun onReceive(message: Any?) {
    when (message) {
      // msg from client
      is ProtoDescriptor.Request -> request(message)
      // msg from server
      is ProtoScMessage.ScMessage -> handleScMessage(message)
      else -> otherMessage(message)
    }
  }


  /** handle msg from client */
  private fun request(msg: ProtoDescriptor.Request) {
    try {
      val csMessage = ProtoCsMessage.CsMessage.parseFrom(msg.req)
      if (csMessage.cmdCase == LOGINREQUEST) { // 登录特殊处理
        login(csMessage.loginRequest)
      } else if (validate(msg)) { // 校验
        when (csMessage.cmdCase) {
          in csMessageToWorld -> forwardToWorld(msg.uid, csMessage)
          else -> forwardToHome(msg.uid, csMessage)
        }
      }
    } catch (e: InvalidProtocolBufferException) {
      error(e)
    }

  }

  /** 错误处理 */
  private fun error(throwable: Throwable) {

  }

  /** 验证消息合法性 */
  private fun validate(message: ProtoDescriptor.Request): Boolean {
    val uid = message.uid
    val token = message.token

    // 验证账号信息使否合法
    // 需要获取PlayerEntity信息，比对账号密码等信息
    return true
  }

  /** 验证玩家账户信息 */
  private fun login(msg: ProtoDescriptor.LoginRequest) {

  }

  private fun forwardToHome(playerId: Long, msg: MessageLite) {
    val envelope = PlayerEnvelope(playerId, msg)
    Gate.homeProxy.tell(envelope, self)
  }

  private fun forwardToWorld(worldId: Long, msg: MessageLite) {
    val envelope = WorldEnvelope(worldId, msg)
    Gate.worldProxy.tell(envelope, self)
  }


  /** handle msg from server(home, world etc.) */
  private fun handleScMessage(scMessage: ProtoScMessage.ScMessage) {
    ProtoDescriptor.Response.newBuilder().apply {
      resp = scMessage.toByteString()
      sendToClient(build())
    }
  }

  private fun otherMessage(message: Any?) {
    when (message) {
      Disconnect -> disconnect()
      is ByteArray -> ctx.write(message) // 大文件直接传输字节流
      is MessageLite -> handleMessageLite(message) // 默认的
      else -> unhandled(message)
    }
  }

  private fun disconnect() {
    Bus.unsubscribeAll(self)
    ctx.disconnect()
  }

  private fun handleMessageLite(message: MessageLite) {
    when (message) {
      is ProtoCommon.Error -> {
        ProtoDescriptor.Response.newBuilder().apply {
          index = 0
          error = message
          sendToClient(build())
        }
      }

    }
  }

  private fun sendToClient(response: ProtoDescriptor.Response) {
    ctx.writeAndFlush(response)
  }

}
