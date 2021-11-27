package com.caidt

import akka.pattern.Patterns
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.BiFunction

class PatternCS() {

  private val duration: Duration = Duration.ofSeconds(10L)

  /************************  remote ask   *******************************/
  /** 用于远程请求 */
  fun <U, T> CompletableFuture<U>.ask(request: Any): CompletionStage<T> {
    @Suppress("UNCHECKED_CAST")
    return Patterns.ask(selection(request), request, duration) as CompletionStage<T>
  }

  /** 表示并发请求 and 表示并发 */
  fun <U, X, T> CompletionStage<U>.andAsk(request: Any, fn: BiFunction<U, X, T>): CompletionStage<T> {
    @Suppress("UNCHECKED_CAST")
    val future = Patterns.ask(selection(request), request, duration) as CompletionStage<X>
    return this.thenCombineAsync(future, fn)
  }

  /** 表示同步请求，为顺序请求 then表示同步操作，需等待 */
  fun <U, T> CompletionStage<U>.thenAsk(): CompletableFuture<T> {
    thenApply { }
    TODO()
  }

  fun <U, T : Any> CompletionStage<U>.accept(): CompletableFuture<T> {
//    thenRunAsync()
    TODO()
  }

  fun <T> CompletionStage<T>.whenComplete(action: (T) -> Unit) {
    whenComplete { t, u ->
      commonException(u) {
        action.invoke(t)
      }
    }
  }

  private fun commonException(throwable: Throwable?, exec: () -> Unit) {
    if (throwable != null) {
      throw IOException("请求出错", throwable)
    } else {
      exec()
    }
  }

}