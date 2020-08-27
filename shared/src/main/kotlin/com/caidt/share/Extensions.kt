package com.caidt.share

import akka.actor.*
import com.caidt.infrastructure.LARGE_MAILBOX
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scala.concurrent.duration.FiniteDuration
import java.time.Duration
import java.util.concurrent.TimeUnit

/** 周期性定时 */
fun Actor.schedule(
  initialDelay: Duration,
  interval: Duration,
  msg: Any,
  sender: ActorRef = ActorRef.noSender()
): Cancellable {
  return context().system().scheduler().schedule(initialDelay, interval, self(), msg, context().dispatcher(), sender)
}

/** 周期性定时 */
fun Actor.schedule(
  initialDelay: Long,
  unit: TimeUnit,
  interval: Long,
  intervalUnit: TimeUnit,
  msg: Any,
  sender: ActorRef = ActorRef.noSender()
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

/** 定时一次 */
fun Actor.scheduleOnce(
  delay: Duration,
  msg: Any,
  sender: ActorRef = ActorRef.noSender()
): Cancellable {
  return context().system().scheduler().scheduleOnce(delay, self(), msg, context().dispatcher(), sender)
}

/** 定时一次 */
fun Actor.scheduleOnce(
  interval: Long,
  intervalUnit: TimeUnit,
  msg: Any,
  sender: ActorRef = ActorRef.noSender()
): Cancellable {
  return context().system().scheduler().scheduleOnce(
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

fun PlayerMessage.wrap(): PlayerEnvelope = PlayerEnvelope(playerId, this)
fun WorldMessage.wrap(): WorldEnvelope = WorldEnvelope(worldId, this)


class NamedRunnable(val name: String, val exec: Runnable)

class Worker : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  companion object {
    fun props(): Props {
      return Props.create(Worker::class.java)
        .withMailbox(LARGE_MAILBOX)
        .withDispatcher("worker-dispatcher")
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
  duration: Long = 1L
) {

  private val timer: Timer = Timer(duration)
  private val jobs: MutableMap<String, Timer> = mutableMapOf()

  fun tick(name: String, duration: Long, exec: () -> Unit) {
    timer.tick {
      jobs.computeIfAbsent(name) { Timer(duration) }
        .tick { submit("${group}_$name", Runnable(exec)) }
    }
  }

  private fun submit(name: String, runnable: Runnable) {
    executor.tellNoSender(NamedRunnable(name, runnable))
  }

}

/** [Tick] 计数器 */
class TickTimer(private val executor: ActorRef, duration: Long) {

  private val timer: Timer = Timer(duration)

  fun tick(exec: () -> Unit) {
    timer.tick {
      executor.tellNoSender(Runnable(exec))
    }
  }

}

/** 计数器 */
class Timer(private val duration: Long) {

  private var tick = 0

  fun tick(exec: () -> Unit) {
    tick++
    if (tick >= duration) {
      exec.invoke()
      tick = 0
    }
  }

}
