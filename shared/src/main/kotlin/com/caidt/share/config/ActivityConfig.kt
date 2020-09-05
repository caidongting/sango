package com.caidt.share.config

import com.caidt.infrastructure.config.DoNotLoad
import com.caidt.infrastructure.config.ExcelConfig
import com.caidt.util.excel.ExcelFile
import com.caidt.util.excel.Row

@DoNotLoad
class ActivityConfig : ExcelConfig() {

  private val activityMap: HashMap<Int, ActivityCfg> = HashMap()

  override fun load() {
    ExcelFile.parse("excel/activity.xlsx") { excel ->
      excel.foreachRow("activity") { row ->
        ActivityCfg.readRow(row).also {
          activityMap[it.type] = it
        }
      }

    }
  }

  override fun afterLoadAll() {
  }

  operator fun get(type: Int): ActivityCfg {
    return checkNotNull(activityMap[type]) { "activity not exist, type:$type" }
  }
}

data class ActivityCfg(
  /** 活动类型 */
  val type: Int,
  /** 活动名称 */
  val name: String,
) {
  companion object {
    fun readRow(row: Row): ActivityCfg {
      val type = row.readInt("活动类型")
      val name = row.readString("活动名称")
      return ActivityCfg(type, name)
    }
  }
}