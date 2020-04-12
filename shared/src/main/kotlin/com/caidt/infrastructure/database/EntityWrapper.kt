package com.caidt.infrastructure.database

import java.io.Serializable
import java.time.Duration

/**
 * wrap the database entity for convenience of use.
 * contain entity or not, unknown now, waiting for more discover
 */
abstract class EntityWrapper<T : IEntity> {

  private lateinit var entity: T

  fun init(entity: T) {
    this.entity = entity
  }

  // primaryKey
  abstract fun primaryKey(): Serializable

  // time duration of data change check
  abstract fun duration(): Duration

  // toEntity for database save
  abstract fun toEntity(): T
}