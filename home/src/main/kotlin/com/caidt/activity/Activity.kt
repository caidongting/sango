package com.caidt.activity

import com.caidt.share.dayStartTime
import java.time.Instant
import java.time.LocalTime

class Activity(
  val uid: Long,
  val type: Int,
  val name: String,
  val startTime: Instant,
  val endTime: Instant
)


enum class Schedule(val day: Int, val time: LocalTime = dayStartTime) {

  DAILY(1),
  WEEK(7),


}