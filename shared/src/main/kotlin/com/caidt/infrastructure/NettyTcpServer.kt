package com.caidt.infrastructure

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.util.AttributeKey


class NettyTcpServer(private val port: Int) {

  fun init() {
    val bootstrap: ServerBootstrap = ServerBootstrap()
      .group(NioEventLoopGroup(), NioEventLoopGroup())
      .channel(NioServerSocketChannel::class.java)
      .childOption(ChannelOption.SO_BACKLOG, 128)
      .childOption(ChannelOption.SO_KEEPALIVE, true)
      .childHandler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          ch.pipeline()
            .addLast(ChannelTrafficShapingHandler(5L))
            .addLast(LengthFieldBasedFrameDecoder(2048, 0, 4))
            .addLast(ProtobufVarint32FrameDecoder())
            .addLast(MyHandler())
        }

      })

    bootstrap.bind(port).sync()
  }
}

@ChannelHandler.Sharable
class MyHandler : ChannelInboundHandlerAdapter() {

  companion object {
    private val ID: AttributeKey<Long> = AttributeKey.valueOf("ID")
    private val session: AttributeKey<ChannelSession> = AttributeKey.valueOf("session")
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    ctx.attr(ID).set(1L)
  }

  override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
    val id = ctx.attr(ID).get()
    val session = ctx.attr(AttributeKey.valueOf<ChannelSession>(id.toString())).get()
  }
}

// 负责与player通信
class ChannelSession
