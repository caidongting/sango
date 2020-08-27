package com.caidt

import akka.actor.ActorRef
import akka.event.japi.LookupEventBus

object Bus {

  private val lookupBus = LookupBus(128)

  fun subscribe(actorRef: ActorRef, channel: Long) {
    lookupBus.subscribe(actorRef, channel)
  }

  fun unsubscribe(actorRef: ActorRef, channel: Long) {
    lookupBus.unsubscribe(actorRef, channel)
  }

  fun unsubscribeAll(actorRef: ActorRef) {
    lookupBus.unsubscribe(actorRef)
  }

  fun publish(channel: Long, msg: Any) {
    lookupBus.publish(Event(channel, msg))
  }
}


internal data class Event(val channel: Long, val msg: Any)

internal class LookupBus(private val size: Int) : LookupEventBus<Event, ActorRef, Long>() {
  override fun mapSize(): Int {
    return size // topic size
  }

  override fun compareSubscribers(a: ActorRef, b: ActorRef): Int {
    return a.compareTo(b)
  }

  override fun classify(event: Event): Long {
    return event.channel
  }

  override fun publish(event: Event, subscriber: ActorRef) {
    subscriber.tell(event.msg, ActorRef.noSender())
  }
}