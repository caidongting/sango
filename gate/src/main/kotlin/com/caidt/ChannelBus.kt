package com.caidt

import akka.actor.ActorRef
import akka.event.japi.LookupEventBus
import akka.protobuf.MessageLite

data class ChannelEvent(
  val channel: Long,
  val msg: MessageLite
)

object ChannelBus : LookupEventBus<ChannelEvent, ActorRef, Long>() {
  override fun mapSize(): Int {
    return 128 // topic size
  }

  override fun compareSubscribers(a: ActorRef, b: ActorRef): Int {
    return a.compareTo(b)
  }

  override fun classify(event: ChannelEvent): Long {
    return event.channel
  }

  override fun publish(event: ChannelEvent, subscriber: ActorRef) {
    subscriber.tell(event.msg, ActorRef.noSender())
  }
}