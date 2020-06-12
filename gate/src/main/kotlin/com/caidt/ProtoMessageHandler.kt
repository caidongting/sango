package com.caidt

import akka.actor.ActorRef
import com.caidt.proto.ProtoCsMessage
import com.caidt.proto.ProtoDescriptor
import com.google.common.util.concurrent.RateLimiter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.naming.AuthenticationException

class ProtoMessageHandler : ChannelInboundHandlerAdapter() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  private val ID: AttributeKey<Long> = AttributeKey.valueOf("ID")
  private val SESSION: AttributeKey<Session> = AttributeKey.valueOf("session")

  @Suppress("UnstableApiUsage")
  private val rateLimiter: RateLimiter = RateLimiter.create(100.0)

  override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
    if (!rateLimiter.tryAcquire()) // 频率检验
      return

    when (msg) {
      is ProtoDescriptor.Ping -> pong(ctx, msg)
      is ProtoDescriptor.Request -> request(ctx, msg)
      is ProtoDescriptor.LoginRequest -> Unit
      else -> unhandled(message = msg)
    }
  }

  private fun unhandled(message: Any?) {
    message?.let {
      logger.error("unhandled message: $it")
    }
  }

  private fun pong(ctx: ChannelHandlerContext, msg: ProtoDescriptor.Ping) {
    ProtoDescriptor.Pong.newBuilder().apply {
      clientMillis = msg.clientMillis
      serverMillis = System.currentTimeMillis()
      ctx.writeAndFlush(build())
    }
  }

  /** 验证消息合法性 */
  private fun validate(request: ProtoDescriptor.Request): Boolean {
    val uid = request.uid
    val token = request.token

    // todo: 验证账号是否存在，然后验证密码是否正确
    // 需要获取PlayerEntity信息，比对账号密码等信息
    return true
  }

  private fun request(ctx: ChannelHandlerContext, request: ProtoDescriptor.Request) {
    if (!validate(request)) throw AuthenticationException("认证失败")

    val csMessage = ProtoCsMessage.CsMessage.parseFrom(request.req)

    val session = ctx.channel().attr(SESSION).get() // getOrCreate Session, dispatcher msg to session
    session.self.tell(csMessage, ActorRef.noSender())
  }

}

