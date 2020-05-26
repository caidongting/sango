package com.caidt.dataContainer

import com.caidt.infrastructure.database.EntityWrapper
import com.caidt.memory.DataContainer
import com.caidt.share.entity.PlayerAccountEntity
import java.io.Serializable
import java.time.Duration

class PlayerDC : DataContainer<PlayerAccountEntity, PlayerAccount>() {

  lateinit var entity: PlayerAccountEntity

  override fun load(entity: PlayerAccountEntity) {
    val account = wrap(entity) { PlayerAccount(it) }
    this.entity = account.entity
  }

}

class PlayerAccount(
  val entity: PlayerAccountEntity
) : EntityWrapper<PlayerAccountEntity> {

  override fun primaryKey(): Serializable = entity.primaryKey()

  override fun duration(): Duration = Duration.ofMinutes(5L)

  override fun toEntity(): PlayerAccountEntity {
    return PlayerAccountEntity(
      playerId = entity.playerId,
      name = entity.name,
      worldId = entity.worldId
    )
  }

}