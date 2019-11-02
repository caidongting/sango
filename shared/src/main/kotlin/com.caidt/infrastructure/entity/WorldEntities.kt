package com.caidt.infrastructure.entity

import com.caidt.infrastructure.database.IEntity


interface WorldEntity : IEntity {
  val worldId: Long
}
