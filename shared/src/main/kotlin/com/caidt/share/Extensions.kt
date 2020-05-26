package com.caidt.share

import akka.actor.*
import com.caidt.infrastructure.LARGE_MAILBOX
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

fun ActorRef.tellNoSender(message: Any) {
  this.tell(message, ActorRef.noSender())
}

fun wrapPlayerEnvelope(playerId: Long, msg: Any): PlayerEnvelope {
  return PlayerEnvelope(playerId, msg)
}

fun wrapWorldEnvelope(worldId: Long, msg: Any): WorldEnvelope {
  return WorldEnvelope(worldId, msg)
}


class NamedRunnable(val name: String, val exec: Runnable)

class Worker : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  companion object {
    fun props(): Props {
      return Props.create(Worker::class.java)
        .withMailbox(LARGE_MAILBOX)
    }
  }

  override fun onReceive(message: Any?) {
    when (message) {
      is NamedRunnable -> {
        message.exec.run()
        logger.info("run task:${message.name}")
      }
      is Runnable -> message.run()
    }
  }
}

/**
 * 一组定时器
 */
class TickDuration(
  private val group: String,
  private val executor: ActorRef,
  private val duration: Long = 1
) {

  private var tick = 0
  private val jobs: MutableMap<String, Ticker> = mutableMapOf()

  fun tick(name: String, duration: Long, exec: () -> Unit) {
    val ticker = jobs.computeIfAbsent(name) { Ticker(duration) }
    tick++
    if (tick >= this.duration) {
      ticker.tick {
        executor.tellNoSender(NamedRunnable("${group}_$name", Runnable(exec)))
      }
      tick = 0
    }
  }

}

class TickTimer(private val executor: ActorRef, duration: Long) {

  private var ticker: Ticker = Ticker(duration)

  fun tick(exec: () -> Unit) {
    ticker.tick {
      executor.tellNoSender(Runnable(exec))
    }
  }

}

// 通过[Tick]触发
class Ticker(private val duration: Long) {

  private var tick = 0

  fun tick(exec: () -> Unit) {
    tick++
    if (tick >= duration) {
      exec.invoke()
      tick = 0
    }
  }

}
