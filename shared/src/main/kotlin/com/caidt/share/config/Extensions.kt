package com.caidt.share.config

import com.caidt.proto.ProtoCommon
import com.caidt.share.common.*
import com.caidt.util.excel.Row

fun Row.readBoolean(column: String): Boolean {
  return this.readString(column) == "是"
}

fun Row.readColor(column: String): ProtoCommon.Color {
  val string = this.readString(column)
  return ProtoCommon.Color.valueOf(string)
}

fun Row.readResourceType(column: String): ProtoCommon.Resource {
  val string = this.readString(column)
  return ProtoCommon.Resource.valueOf(string)
}

fun Row.readItemType(column: String): ProtoCommon.ItemType {
  val string = this.readString(column)
  return ProtoCommon.ItemType.valueOf(string)
}

// =================== complex =====================

internal val bracketRegex = "[\\[\\]]".toRegex()

fun Row.readList(column: String): List<String> {
  val string = this.readString(column)
  if (string.isBlank()) return emptyList()
  return string.replace(bracketRegex, "").split(",")
}

fun Row.readItemData(column: String): ItemData {
  val string = this.readString(column)
  val (itemId, count) = string.split("=").map { it.toInt() }
  return ItemData(itemId, count.toLong())
}

fun Row.readResource(column: String): ResourceData {
  TODO()
}

fun Row.readPackage(column: String): RewardPackage {
  TODO()
}