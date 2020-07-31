package com.caidt.share.common

import com.caidt.proto.ProtoCommon

class DisplayItem(
  val id: Int,
  val count: Long
) {

  operator fun plus(item: DisplayItem): DisplayItem {
    return DisplayItem(id, count + item.count)
  }
}

class DisplayResource(
  val type: ProtoCommon.Resource,
  var count: Long
)


/** 通用礼包 */
class RewardPackage private constructor(
  val items: List<DisplayItem>,
  val resources: List<DisplayResource>
) {

  fun builder(): Builder = Builder(this)

  fun getCount(resource: ProtoCommon.Resource): Long {
    return resources.filter { it.type == resource }.map { it.count }.sum()
  }

  fun getCount(itemId: Int): Long {
    return items.filter { it.id == itemId }.map { it.count }.sum()
  }

  companion object {
    fun newBuilder(): Builder = Builder()
  }

  class Builder() {
    private val items: MutableList<DisplayItem> = mutableListOf()
    private val resources: MutableList<DisplayResource> = mutableListOf()

    constructor(rewardPackage: RewardPackage) : this() {
      items.addAll(rewardPackage.items)
      resources.addAll(rewardPackage.resources)
    }

    operator fun plus(item: DisplayItem): Builder {
      items.add(item)
      return this
    }

    operator fun plus(items: List<DisplayItem>): Builder {
      this.items.addAll(items)
      return this
    }

    operator fun plus(resource: DisplayResource): Builder {
      resources.add(resource)
      return this
    }

    operator fun plus(rewardPackage: RewardPackage): Builder {
      items.addAll(rewardPackage.items)
      resources.addAll(rewardPackage.resources)
      return this
    }

    fun merge(): Builder {
      // todo: merge same id count
      return this
    }

    fun build(): RewardPackage {
      return RewardPackage(items, resources)
    }
  }

}
