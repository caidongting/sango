package com.caidt

import com.caidt.proto.ProtoDescriptor
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.codec.protobuf.*
import io.netty.handler.traffic.ChannelTrafficShapingHandler


class NettyTcpServer(private val port: Int) {

  fun start() {
    val bootstrap: ServerBootstrap = ServerBootstrap()
      .group(NioEventLoopGroup(), NioEventLoopGroup())
      .channel(NioServerSocketChannel::class.java)
      .childOption(ChannelOption.SO_BACKLOG, 128)
      .childOption(ChannelOption.SO_KEEPALIVE, true)
      .childHandler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
          ch.pipeline()
            .addLast(ChannelTrafficShapingHandler(5L))
            // decoder
            .addLast(LengthFieldBasedFrameDecoder(2048, 0, 4))
            .addLast(ProtobufVarint32FrameDecoder())
            // AES decrypt todo
            .addLast(ProtobufDecoder(ProtoDescriptor.Request.getDefaultInstance()))
            .addLast(ByteArrayDecoder()) // 原则上禁止，这里仅做对比
            .addLast(ProtoHandler())
            .addLast(ProtoMessageHandler())

            // encoder
            .addLast(LengthFieldPrepender(4))
            // AES encrypt todo
            .addLast(ProtobufEncoder())
            .addLast(ByteArrayEncoder()) // 直接返回字节数组，如大数据 战报，文件等
        }

      })

    bootstrap.bind(port).sync()
  }
}

