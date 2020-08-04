package com.caidt.share.entity

import com.caidt.infrastructure.NoArg
import com.caidt.infrastructure.database.IEntity
import com.caidt.proto.ProtoCommon
import java.io.Serializable
import javax.persistence.*

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
  var name: String,

  @Column(name = "world_id")
  var worldId: Long
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return this.playerId
  }
}

@NoArg
data class ItemPk(
  @Id
  @Column(name = "uid")
  val uid: Long,
  @Id
  @Column(name = "player_id")
  override val playerId: Long
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return this
  }
}

@Entity
@Table(name = "item")
@IdClass(ItemPk::class)
data class ItemEntity(
  @Id
  @Column(name = "uid")
  val uid: Long,

  @Id
  @Column(name = "player_id")
  override val playerId: Long,

  @Column(name = "item_id")
  val itemId: Int,

  @Column(name = "count")
  var count: Long
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return ItemPk(uid, playerId)
  }
}

@NoArg
data class ResourcePk(
  @Id
  @Column(name = "player_id")
  override val playerId: Long,

  @Id
  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  val type: ProtoCommon.Resource
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return this
  }
}

@Entity
@Table(name = "resource")
@IdClass(ResourcePk::class)
data class ResourceEntity(
  @Id
  @Column(name = "player_id")
  override val playerId: Long,

  @Id
  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  val type: ProtoCommon.Resource,

  @Column(name = "count")
  var count: Long
) : PlayerEntity {
  override fun primaryKey(): Serializable {
    return ResourcePk(playerId, type)
  }
}
