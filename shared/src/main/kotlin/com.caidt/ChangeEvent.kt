package com.caidt

interface ChangeEvent


data class PowerChangeEvent(val power: Long) : ChangeEvent

class PlayerEvent(
  val playerId: Long,
  val playerLevel: Int,
  val playerExp: Long,
  val playerVip: Int
)