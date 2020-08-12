package com.caidt.share.config

import com.caidt.infrastructure.config.ExcelConfig
import com.caidt.proto.ProtoCommon
import com.caidt.util.excel.ExcelFile
import com.caidt.util.excel.Row

/**
 * 道具配置
 */
class ItemConfig : ExcelConfig() {

  /** 道具 */
  private val itemMap: HashMap<Int, ItemCfg> = HashMap()
  private val itemNameMap: HashMap<String, ItemCfg> = HashMap()

  override fun load() {
    ExcelFile.parse("excel/item.xlsx") { sheet ->
      sheet.foreachRow("道具") { row ->
        val itemCfg = ItemCfg.readRow(row)
        itemMap[itemCfg.id] = itemCfg
        itemNameMap[itemCfg.name] = itemCfg
      }
    }
  }

  override fun afterLoadAll() {
  }


  operator fun get(id: Int): ItemCfg {
    return checkNotNull(itemMap[id]) { "itemCfg not found, id=$id" }
  }

  operator fun get(name: String): ItemCfg {
    return checkNotNull(itemNameMap[name]) { "itemCfg not found, name=$name" }
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
  /** 是否唯一道具 */
  val unique: Boolean
) {

  companion object {
    fun readRow(row: Row): ItemCfg {
      val itemId = row.readInt("道具id")
      val name = row.readString("道具名称")
      val itemType = ProtoCommon.ItemType.valueOf(row.readString("道具类型"))
      val subType = row.readInt("子类型")
      val color = ProtoCommon.Color.valueOf(row.readString("道具品质"))
      val unique = row.readBoolean("唯一道具")
      return ItemCfg(itemId, name, itemType, subType, color, unique)
    }
  }

}
