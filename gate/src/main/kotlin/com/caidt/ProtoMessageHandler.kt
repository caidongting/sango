package com.caidt

import akka.actor.ActorRef
import com.caidt.proto.ProtoDescriptor
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProtoMessageHandler : ChannelInboundHandlerAdapter() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  private val ID: AttributeKey<Long> = AttributeKey.valueOf("ID")
  private val SESSION: AttributeKey<Session> = AttributeKey.valueOf("session")


  override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
    when (msg) {
      is ProtoDescriptor.Ping -> pong(ctx, msg)
      is ProtoDescriptor.Request -> {
        val session = ctx.channel().attr(SESSION).get() // getOrCreate Session, dispatcher msg to session
        session.self.tell(msg, ActorRef.noSender())
      }
      else -> unhandled(message = msg)
    }
  }

  private fun unhandled(message: Any?) {
    logger.error("unhandled message: $message")
  }

  private fun pong(ctx: ChannelHandlerContext, msg: ProtoDescriptor.Ping) {
    ProtoDescriptor.Pong.newBuilder().apply {
      clientMillis = msg.clientMillis
      serverMillis = System.currentTimeMillis()
      ctx.writeAndFlush(build())
    }
  }


}

