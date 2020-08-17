package com.caidt.share.common

import com.caidt.infrastructure.config.ExcelConfigs
import com.caidt.proto.ProtoCommon
import com.caidt.share.config.ItemCfg

// 道具 作为配置数据，不可变
data class ItemData(
  val id: Int,
  val count: Long
) {

  val cfg: ItemCfg get() = ExcelConfigs.itemConfig[id]
  val name: String get() = cfg.name

  operator fun plus(itemData: ItemData): ItemData {
    return this.copy(count = count + itemData.count)
  }
}

// 资源 作为配置数据，不可变
data class ResourceData(
  val type: ProtoCommon.Resource,
  val count: Long
) {

  operator fun plus(resourceData: ResourceData): ResourceData {
    return ResourceData(type, count + resourceData.count)
  }

}


/** 通用礼包 不可变 */
class RewardPackage private constructor(
  val items: List<ItemData>,
  val resources: List<ResourceData>
) {

  fun builder(): Builder = newBuilder() + this

  fun getCount(resource: ProtoCommon.Resource): Long {
    return resources.filter { it.type == resource }.map { it.count }.sum()
  }

  fun getCount(itemId: Int): Long {
    return items.filter { it.id == itemId }.map { it.count }.sum()
  }

  companion object {
    fun newBuilder(): Builder = Builder.create()
  }

  class Builder private constructor() {
    private val items: MutableList<ItemData> = mutableListOf()
    private val resources: MutableList<ResourceData> = mutableListOf()

    companion object {
      fun create(): Builder = Builder()
    }

    operator fun plus(itemData: ItemData): Builder {
      this.items.add(itemData)
      return this
    }

    operator fun plus(resource: ResourceData): Builder {
      this.resources.add(resource)
      return this
    }

    operator fun plus(rewardPackage: RewardPackage): Builder {
      addItems(rewardPackage.items)
      addResources(rewardPackage.resources)
      return this
    }

    fun merge(): Builder {
      // todo: merge same id count
      return this
    }

    fun build(): RewardPackage {
      return RewardPackage(items, resources)
    }

    fun addItems(itemData: Collection<ItemData>): Builder {
      this.items.addAll(itemData)
      return this
    }

    fun addResources(resourceData: Collection<ResourceData>): Builder {
      this.resources.addAll(resourceData)
      return this
    }
  }

}

fun main() {
  val newBuilder: RewardPackage.Builder = RewardPackage.newBuilder()
  newBuilder.build()
}