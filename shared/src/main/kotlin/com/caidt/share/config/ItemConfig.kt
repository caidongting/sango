package com.caidt.share.config

import com.caidt.infrastructure.config.DoNotLoad
import com.caidt.infrastructure.config.ExcelConfig
import com.caidt.proto.ProtoCommon
import com.caidt.util.excel.ExcelFile
import com.caidt.util.excel.Row

/**
 * 道具配置
 */
@DoNotLoad
class ItemConfig : ExcelConfig() {

  /** 道具 <itemId, cfg> */
  private val itemMap: HashMap<Int, ItemCfg> = HashMap()
  /** 道具 <ItemName, cfg> */
  private val itemNameMap: HashMap<String, ItemCfg> = HashMap()

  override fun load() {
    ExcelFile.parse("excel/item.xlsx") { excel ->
      excel.foreachRow("道具") { row ->
        ItemCfg.readRow(row).also {
          itemMap[it.id] = it
          itemNameMap[it.name] = it
        }
      }
    }
  }

  override fun afterLoadAll() {
  }


  operator fun get(id: Int): ItemCfg {
    return requireNotNull(itemMap[id]) { "itemCfg not found, id=$id" }
  }

  operator fun get(name: String): ItemCfg {
    return requireNotNull(itemNameMap[name]) { "itemCfg not found, name=$name" }
  }

}

data class ItemCfg(
  /** 道具id */
  val id: Int,
  /** 道具名称 */
  val name: String,
  /** 道具类型 */
  val type: ProtoCommon.ItemType,
  /** 子类型 */
  val subType: Int,
  /** 道具品质 */
  val color: ProtoCommon.Color,
  /** 堆叠上限 */
  val limit: Int,
) {

  /** 是否唯一道具 */
  val unique: Boolean get() = limit == 1

  companion object {
    fun readRow(row: Row): ItemCfg {
      val itemId = row.readInt("道具id")
      val name = row.readString("道具名称")
      val itemType = row.readItemType("道具类型")
      val subType = row.readInt("子类型")
      val color = row.readColor("道具品质")
      val limit = row.readInt("堆叠上限")
      return ItemCfg(itemId, name, itemType, subType, color, limit)
    }
  }

}
