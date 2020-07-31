package com.caidt

import akka.actor.ActorRef
import akka.pattern.Patterns
import java.time.Duration
import java.util.concurrent.CompletionStage

class PatternCS(
  val actor: ActorRef
) {

  private val duration: Duration = Duration.ofSeconds(10L)

  fun <U> ask(msg: Any): CompletionStage<U> {
    return Patterns.ask(actor, msg, duration) as CompletionStage<U>
  }

  fun <U, T> andAsk(msg: Any): CompletionStage<T> {
    TODO()
  }

}