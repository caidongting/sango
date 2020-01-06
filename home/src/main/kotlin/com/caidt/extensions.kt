package com.caidt

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import com.caidt.infrastructure.Ok
import scala.concurrent.duration.FiniteDuration
import java.time.Duration
import java.util.concurrent.TimeUnit

fun Actor.schedule(
  initialDelay: Duration,
  interval: Duration,
  msg: Any,
  sender: ActorRef = ActorRef.noSender()
): Cancellable {
  return context().system().scheduler().schedule(initialDelay, interval, self(), msg, context().dispatcher(), sender)
}

fun Actor.schedule(
  initialDelay: Long, unit: TimeUnit, interval: Long, intervalUnit: TimeUnit,
  msg: Any, sender: ActorRef = ActorRef.noSender()
): Cancellable {
  return context().system().scheduler().schedule(
    FiniteDuration(initialDelay, unit),
    FiniteDuration(interval, intervalUnit),
    self(),
    msg,
    context().dispatcher(),
    sender
  )
}


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}

fun ActorRef.tellNoSender(message: Any) {
  this.tell(message, ActorRef.noSender())
}