package com.caidt

import java.time.Instant
import java.time.LocalTime

class Activity(
  val uid: Long,
  val type: Int,
  val name: String,
  val startTime: Instant,
  val endTime: Instant
)


enum class Schedule(val day: Int, val time: LocalTime = LocalTime.of(5, 0)) {

  DAILY(1),
  WEEK(7),


}