package com.caidt

import com.caidt.proto.ProtoDescriptor
import com.google.common.util.concurrent.RateLimiter
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder

class ProtoHandler : MessageToMessageDecoder<ProtoDescriptor.Request>() {

  private var index = 0

  @Suppress("UnstableApiUsage")
  private val rateLimiter: RateLimiter = RateLimiter.create(100.0)

  override fun decode(ctx: ChannelHandlerContext, msg: ProtoDescriptor.Request, out: MutableList<Any>) {
    @Suppress("UnstableApiUsage")
    if (!rateLimiter.tryAcquire()) // 频率检验
      return

    if (msg.index != index) { // 索引校验
      return
    }

    index++

    out.add(msg)

  }


}