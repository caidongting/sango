package com.caidt.activity

import com.caidt.infrastructure.config.ExcelConfigs
import com.caidt.share.config.ActivityCfg
import com.caidt.share.dayStartTime
import java.time.Instant
import java.time.LocalTime

/**
 * 活动外部显示
 */
interface ActivityClient {
  /** 活动名称 */
  val name: String

  /** 开始时间 */
  val startTime: Instant

  /** 结束时间 */
  val endTime: Instant
}

/**
 * 通用活动
 */
abstract class Activity(val uid: Long, val type: Int) : ActivityClient {

  val cfg: ActivityCfg get() = ExcelConfigs.activityConfig[type]

  override val name: String get() = cfg.name
}

/**
 * 常规活动 可以合并到Activity
 */
class RegularActivity(uid: Long, type: Int) : Activity(uid, type) {

  init {
    cfg.type
  }

  override val startTime: Instant
    get() = TODO("Not yet implemented")
  override val endTime: Instant
    get() = TODO("Not yet implemented")
}

/**
 * 周期活动
 */
class CycleActivity(uid: Long, type: Int) : Activity(uid, type) {
  override val startTime: Instant
    get() = TODO("Not yet implemented")
  override val endTime: Instant
    get() = TODO("Not yet implemented")
}


enum class Schedule(val day: Int, val time: LocalTime = dayStartTime) {
  DAILY(1),
  WEEK(7),
}