package com.caidt

import akka.actor.ActorRef
import akka.actor.Props
import com.caidt.proto.ProtoCsMessage
import com.caidt.proto.ProtoDescriptor
import io.netty.channel.*
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.naming.AuthenticationException

@ChannelHandler.Sharable
class ProtoMessageHandler : SimpleChannelInboundHandler<ProtoDescriptor.Request>() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  companion object {
    private val ID: AttributeKey<Long> = AttributeKey.valueOf("ID")
    private val SESSION: AttributeKey<ActorRef> = AttributeKey.valueOf("session")
  }

//  override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
//    when (msg) {
//      is ProtoDescriptor.Ping -> pong(ctx, msg)
//      is ProtoDescriptor.Request -> request(ctx, msg)
//      is ProtoDescriptor.LoginRequest -> Unit
//      else -> unhandled(message = msg)
//    }
//  }

  override fun channelRead0(ctx: ChannelHandlerContext, msg: ProtoDescriptor.Request) {
    if (!validate(msg)) throw AuthenticationException("认证失败")

    ctx.channel().attr(ID).setIfAbsent(msg.uid)
    if (!ctx.channel().hasAttr(SESSION)) {
      val actorRef = Gate.actorSystem.actorOf(Props.create(ChannelSession::class.java, ctx))
      ctx.channel().attr(SESSION).set(actorRef)
    }
    try {
      val session = ctx.channel().attr(SESSION).get()
      val csMessage = ProtoCsMessage.CsMessage.parseFrom(msg.req)
      session.tell(csMessage, ActorRef.noSender())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun handleMessage() {

  }

  /** 验证消息合法性 */
  private fun validate(request: ProtoDescriptor.Request): Boolean {
    val uid = request.uid
    val token = request.token

    // todo: 验证账号是否存在，然后验证密码是否正确
    // 需要获取PlayerEntity信息，比对账号密码等信息
    return true
  }

  private fun unhandled(message: Any?) {
    message?.let {
      logger.error("unhandled message: $it")
    }
  }

//  private fun pong(ctx: ChannelHandlerContext, msg: ProtoDescriptor.Ping) {
//    ProtoDescriptor.Pong.newBuilder().apply {
//      clientMillis = msg.clientMillis
//      serverMillis = System.currentTimeMillis()
//      ctx.writeAndFlush(build())
//    }
//  }


}

