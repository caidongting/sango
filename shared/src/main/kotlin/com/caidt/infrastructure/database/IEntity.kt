package com.caidt.infrastructure.database

import java.io.Serializable

interface IEntity : Serializable {
  fun primaryKey(): Serializable
}