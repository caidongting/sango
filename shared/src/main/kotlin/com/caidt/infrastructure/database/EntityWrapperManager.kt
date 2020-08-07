package com.caidt.infrastructure.database

import com.caidt.share.Ticker
import java.time.Instant
import java.util.*

object EntityWrapperManager {

  private val trackerList: MutableList<Tracker> = LinkedList()

  private val pendingQueue: Queue<Operation> = LinkedList()

  private const val BATCH_SIZE = 1000
  /** 定时刷新间隔(s) */
  private const val DURATION_FLUSH = 600L

  private val ticker = Ticker(DURATION_FLUSH) // flush every 10 minutes (default)

  fun update(wrapper: EntityWrapper<*>) {
    createTracker(OP.UPDATE, wrapper)
  }

  fun save(wrapper: EntityWrapper<*>) {
    createTracker(OP.SAVE, wrapper)
  }

  fun delete(wrapper: EntityWrapper<*>) {
    createTracker(OP.DELETE, wrapper)
  }

  private fun createTracker(op: OP, wrapper: EntityWrapper<*>) {
    trackerList.add(Tracker(op, wrapper))
  }

  fun tick(now: Instant) {
    // 1. 定时检测数据变化
    tickChange(now)

    // 2. 定时刷新pendingQueue
    ticker.tick { flush() }
  }

  private fun tickChange(now: Instant) {
    for (tracker in trackerList) {
      // 检测时间
      if (tracker.checkTime.isBefore(now)) {
        tracker.checkTime = Instant.now()
        val entity = tracker.wrapper.toEntity()
        // todo: ensure entity content has been changed
        // compare hashcode / custom hashcode / content hashcode

        pending(tracker.op, entity, now)
      }
    }
  }

  private fun pending(op: OP, entity: IEntity, now: Instant) {
    val pre = pendingQueue.poll()
    if (pre != null && pre.entity.primaryKey() == entity.primaryKey()) {

    }
    pendingQueue.add(Operation(op, entity))
    // todo: 合并操作 对于同一数据的操作(通过primaryKy判断)
    //  1. 两个update操作可以合并（去掉第一个update操作，因为此处使用全量更新），
    //  2. 先insert后update可以直接insert后一个数据
    //  3. 先其他操作，后delete，则前一个操作可以去掉
    //  4. 错误情况：1.中途insert 2.delete后update/delete

    if (pendingQueue.size >= BATCH_SIZE) { // 到达一定大小后强制刷新
      flush()
    }
  }

  private fun flush() {
    while (!pendingQueue.none()) {
      val e = pendingQueue.poll()
      // todo: 保存数据
    }
  }

}

enum class OP {
  SAVE,
  UPDATE,
  DELETE,
  ;
}


data class Operation(val op: OP, val entity: IEntity)

data class Tracker(val op: OP, val wrapper: EntityWrapper<*>) {

  var checkTime: Instant = Instant.now()
    get() = field.plus(wrapper.duration())

}
