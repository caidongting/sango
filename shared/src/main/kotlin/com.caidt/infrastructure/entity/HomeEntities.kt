@file:Suppress("JpaObjectClassSignatureInspection")

package com.caidt.infrastructure.entity

import com.caidt.infrastructure.database.IEntity
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

interface PlayerEntity : IEntity {
  val playerId: Long
}


@Entity
@Table(name = "player_account")
data class PlayerAccountEntity(
    @Id
    @Column(name = "player_id")
    override val playerId: Long,

    @Column(name = "name")
    var name: String
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return this.playerId
  }
}
