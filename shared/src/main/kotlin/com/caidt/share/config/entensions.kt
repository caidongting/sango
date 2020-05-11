package com.caidt.share.config

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