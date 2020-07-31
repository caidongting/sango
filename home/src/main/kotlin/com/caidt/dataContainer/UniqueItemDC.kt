package com.caidt.dataContainer

import com.caidt.infrastructure.database.EntityWrapper
import com.caidt.memory.DataContainer
import com.caidt.share.entity.ItemEntity
import com.caidt.share.entity.ItemPk
import java.io.Serializable
import java.time.Duration

class UniqueItemDC : DataContainer<ItemEntity, UniqueItem>() {
  override fun load(entity: ItemEntity) {
    TODO("Not yet implemented")
  }

}

class UniqueItem(
  val uid: Long,
  val id: Long,
  val owner: Long,
  var count: Long
) : EntityWrapper<ItemEntity> {
  override fun primaryKey(): Serializable {
    return ItemPk(uid, owner)
  }

  override fun duration(): Duration {
    TODO("Not yet implemented")
  }

  override fun toEntity(): ItemEntity {
    TODO("Not yet implemented")
  }

}
