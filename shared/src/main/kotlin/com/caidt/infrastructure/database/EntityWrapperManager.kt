package com.caidt.infrastructure.database

import com.caidt.share.Timer
import java.time.Instant
import java.util.*

class EntityWrapperManager(private val session: Session) {

  private val trackerList: MutableList<Tracker> = LinkedList()

  private val pendingQueue: Queue<Operation> = LinkedList()

  companion object {
    /** 批量执行数量 */
    private const val BATCH_SIZE = 1000

    /** 定时刷新间隔(s) */
    private const val DURATION_FLUSH = 600L
  }

  private val ticker = Timer(DURATION_FLUSH) // flush every 10 minutes (default)

  fun createTracker(clazz: Class<*>, wrapper: EntityWrapper<*>) {
    trackerList.add(Tracker(clazz, wrapper, Instant.now()))
  }

  fun removeTracker(clazz: Class<*>, wrapper: EntityWrapper<*>) {
    trackerList.removeIf {
      it.clazz == clazz &&
          it.snapshot.primaryKey() == wrapper.primaryKey()
    }
  }

  fun save(clazz: Class<*>, wrapper: EntityWrapper<*>) {
    pending(OP.SAVE, wrapper.toEntity())
    createTracker(clazz, wrapper)
  }

  fun delete(clazz: Class<*>, wrapper: EntityWrapper<*>) {
    pending(OP.DELETE, wrapper.toEntity())
    removeTracker(clazz, wrapper)
  }

  fun tick(now: Instant) {
    // 1. 定时检测数据变化
    tickChange(now)

    // 2. 定时刷新pendingQueue
    ticker.tick { flush() }
  }

  private fun hash(entity: IEntity): Long {
    return 0L
  }

  private fun tickChange(now: Instant) {
    for (tracker in trackerList) {
      // 检测时间
      if (tracker.checkTime.isBefore(now)) {
        tracker.lastCheckTime = Instant.now()

        val entity = tracker.wrapper.toEntity()
        val snapshot = tracker.snapshot
        //  ensure entity content has been changed
        // compare hashcode / custom hashcode / content hashcode
        if (Objects.hash(snapshot) != Objects.hash(entity) ||
          hash(snapshot) != hash(entity) ||
          snapshot.hashCode() != entity.hashCode()
        ) {
          pending(OP.UPDATE, entity)
        }
      }
    }
  }

  private fun pending(op: OP, entity: IEntity) {
    // val pre = pendingQueue.peek()
    // if (pre != null && pre.entity.primaryKey() == entity.primaryKey()) {
    //
    // }
    pendingQueue.offer(Operation(op, entity))
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
      val (op, entity) = pendingQueue.poll()
      when (op) {
        OP.SAVE -> session.saveOrUpdate(entity)
        OP.UPDATE -> session.saveOrUpdate(entity)
        OP.DELETE -> session.delete(entity)
      }
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

data class Tracker(val clazz: Class<*>, val wrapper: EntityWrapper<*>, var lastCheckTime: Instant) {
  val snapshot: IEntity = wrapper.toEntity()
  val checkTime: Instant get() = lastCheckTime.plus(wrapper.duration())
}
