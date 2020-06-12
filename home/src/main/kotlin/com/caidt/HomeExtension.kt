package com.caidt

import akka.actor.ActorSelection
import akka.pattern.Patterns
import com.caidt.share.*
import com.google.protobuf.MessageLite
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.BiFunction


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}

/** 向其他玩家发送消息 */
fun PlayerActor.sendToPlayer(playerId: Long, msg: Any) {
  val envelope = PlayerEnvelope(playerId, msg)
  Home.homeProxy.tell(envelope, self)
}

fun PlayerActor.answer() {

}

fun PlayerActor.sendMessage(msg: Any) {
  when (msg) {
    is MessageLite -> sendToClient(msg)
    is PlayerMessage -> sendMessageToHome(msg.wrap())
    is PlayerEnvelope -> sendMessageToHome(msg)
    is WorldMessage -> sendMessageToWorld(msg.wrap())
    is WorldEnvelope -> sendMessageToWorld(msg)
    else -> unhandled(msg)
  }
}

internal fun PlayerActor.sendMessageToHome(msg: PlayerEnvelope) {
  Home.homeProxy.tell(msg, self)
}

internal fun PlayerActor.sendMessageToWorld(msg: WorldEnvelope) {
  Home.worldProxy.tell(msg, self)
}

internal fun selection(request: Any): ActorSelection {
  return when (request) {
    is PlayerMessage -> Home.homeSelection
    is WorldMessage -> Home.WorldSelection
    else -> throw RuntimeException()
  }
}

val duration: Duration = Duration.ofSeconds(10L)

/************************  remote ask   *******************************/
/** 用于远程请求 */
fun <U, T> CompletionStage<U>.ask(request: Any): CompletionStage<T> {
  return Patterns.ask(selection(request), request, duration) as CompletionStage<T>
}

/** 表示并发请求 and 表示并发 */
fun <U, X, T> CompletableFuture<U>.andAsk(request: Any, fn: BiFunction<U, X, T>): CompletionStage<T> {
  val future = Patterns.ask(selection(request), request, duration) as CompletionStage<X>
  return this.thenCombine(future, fn)
}

/** 表示同步请求，为顺序请求 then表示同步操作，需等待 */
fun <U, T> CompletableFuture<U>.thenAsk(): CompletableFuture<T> {
  thenApply { }
  TODO()
}

fun <U, T : Any> CompletableFuture<U>.accept(): CompletableFuture<T> {
//    thenRunAsync()
  TODO()
}

fun <T> CompletableFuture<T>.whenComplete(action: (T) -> Unit) {
  whenComplete { t, u ->
    commonException(u) {
      action.invoke(t)
    }
  }
}

fun commonException(throwable: Throwable?, exec: () -> Unit) {
  if (throwable != null) {
    throw IOException("请求出错", throwable)
  } else {
    exec()
  }
}


