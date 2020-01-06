package com.caidt

import com.caidt.infrastructure.PlayerId


interface ChangeEvent


data class PowerChangeEvent(val power: Long) : ChangeEvent

class PlayerEvent(
  val playerId: PlayerId,
  val playerLevel: Int,
  val playerExp: Long,
  val playerVip: Int
)