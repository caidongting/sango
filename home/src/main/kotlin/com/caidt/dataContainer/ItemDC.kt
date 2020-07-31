package com.caidt.dataContainer

import com.caidt.infrastructure.database.EntityWrapper
import com.caidt.memory.DataContainer
import com.caidt.share.entity.ItemEntity
import com.caidt.share.entity.ItemPk
import java.io.Serializable
import java.time.Duration

class ItemDC : DataContainer<ItemEntity, Item>() {
  override fun load(entity: ItemEntity) {
    TODO("Not yet implemented")
  }

}

class Item(
  val id: Long,
  val owner: Long,
  var count: Long
) : EntityWrapper<ItemEntity> {
  override fun primaryKey(): Serializable {
    return ItemPk(id, owner)
  }

  override fun duration(): Duration {
    TODO("Not yet implemented")
  }

  override fun toEntity(): ItemEntity {
    TODO("Not yet implemented")
  }

}
