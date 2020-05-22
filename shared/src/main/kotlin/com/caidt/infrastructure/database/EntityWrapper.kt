package com.caidt.infrastructure.database

import java.io.Serializable
import java.time.Duration

/**
 * wrap the database entity for convenience of use.
 * contain entity or not, unknown now, waiting for more discover
 */
interface EntityWrapper<T : IEntity> {

  // primaryKey
  fun primaryKey(): Serializable

  // time duration of data change check
  fun duration(): Duration

  // toEntity for database save
  fun toEntity(): T
}