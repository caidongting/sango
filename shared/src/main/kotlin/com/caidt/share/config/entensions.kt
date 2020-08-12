package com.caidt.share.config

import com.caidt.share.common.*
import com.caidt.util.excel.Row

internal val bracketRegex = "[\\[\\]]".toRegex()

fun Row.readList(column: String): List<String> {
  val string = this.readString(column)
  if (string.isBlank()) return emptyList()
  return string.replace(bracketRegex, "").split(",")
}

fun Row.readBoolean(column: String): Boolean {
  return this.readString(column) == "是"
}

fun Row.readItem(column: String): ItemData {
  TODO()
}

fun Row.readResource(column: String): ResourceData {
  TODO()
}

fun Row.readPackage(column: String): RewardPackage {
  TODO()
}