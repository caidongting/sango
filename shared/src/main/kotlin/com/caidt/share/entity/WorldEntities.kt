package com.caidt.share.entity

import com.caidt.infrastructure.database.IEntity


interface WorldEntity : IEntity {
  val worldId: Long
}