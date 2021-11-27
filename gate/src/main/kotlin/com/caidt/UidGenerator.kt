package com.caidt

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import akka.routing.ActorRefRoutee
import akka.routing.BalancingRoutingLogic
import akka.routing.Routee
import akka.routing.Router
import com.caidt.share.GenerateUid
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UidGenerator : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  private lateinit var router: Router

  override fun preStart() {
    val routee = ArrayList<Routee>()
    repeat(UidWorker.WORKER_NUM) { id ->
      val actorOf = context.actorOf(Props.create(UidWorker::class.java, id), "uidWorker-$id")
      context.watch(actorOf)
      routee.add(ActorRefRoutee(actorOf))
    }
    router = Router(BalancingRoutingLogic(), routee)
  }

  override fun onReceive(message: Any?) {
    when (message) {
      GenerateUid -> router.route(message, sender)
      else -> unhandled(message)
    }
  }

}

/**
 * 使用雪花算法生成UID todo: 确定顺序
 * sign |workerId|-----------------timestamp--------------------|-sequenceId-|
 *    0      0000 0000000000000000000000000000000000000000000000 000000000000
 *    1 |---4----|---------------------47-----------------------|-----12-----|
 * sign: 符号位，1位，固定为 0
 * workerId: worker id, 位数 4~6
 * timestamp: 时间戳，=为生成时间戳 - 1970时间戳，位数 47，可表示范围 todo:
 * sequenceId: 序列号，表示相同时间戳下可以生成的uid个数，位数 10~12
 */
class UidWorker(id: Int) : UntypedAbstractActor() {

  companion object {
    /** 工作id位数 4~6 */
    private const val WORKER_BITS = 4

    /** max num 16, range 0~15 */
    const val WORKER_NUM = 1 shl WORKER_BITS

    /** 时间戳序列 */
    const val TIMESTAMP_BITS = 47

    /** 序列号位数 表示每ms可生成的uid个数 (2^n) */
    const val SEQUENCE_BITS = Long.SIZE_BITS - 1 - TIMESTAMP_BITS - WORKER_BITS

    /** 最大序号 */
    const val MAX_SEQUENCE = 1 shl SEQUENCE_BITS

    /** 回拨时间 */
    const val TIME_OFFSET = 2000
  }

  override fun onReceive(message: Any?) {
    when (message) {
      GenerateUid -> sender.tell(generateUid(), ActorRef.noSender())
      else -> unhandled(message)
    }
  }

  private val workerId: Long = id.toLong() shl (TIMESTAMP_BITS + SEQUENCE_BITS)

  private var currentMillis: Long = 0L
  private var sequence: Long = 0L

  private fun generateUid(): Long {
    var nowMillis = System.currentTimeMillis()
    if (nowMillis < currentMillis) {
      if (currentMillis - nowMillis < TIME_OFFSET) {
        // 容忍指定的回拨，避免NTP校时造成的异常
        nowMillis = currentMillis
      } else {
        throw RuntimeException("Clock moved backwards.Refusing to generate id for ${currentMillis - nowMillis}ms")
      }
    }

    if (currentMillis != nowMillis) { // 可能有指针回拨
      currentMillis = nowMillis
      sequence = 0
    } else if (++sequence >= MAX_SEQUENCE) { // 序号 +1
      // 等待到下一秒 currentMillis = nextMillis
      currentMillis = tilNextMillis(nowMillis)
      sequence = 0
    }
    return workerId or currentMillis.shl(SEQUENCE_BITS) or sequence
  }

  private fun tilNextMillis(nowMillis: Long): Long {
    var currentTimeMillis: Long
    do {
      currentTimeMillis = System.currentTimeMillis()
    } while (nowMillis == currentTimeMillis)

    if (nowMillis < currentMillis) {
      throw RuntimeException("Clock moved backwards.Refusing to generate id for ${currentMillis - nowMillis}ms")
    }
    return currentTimeMillis
  }

}