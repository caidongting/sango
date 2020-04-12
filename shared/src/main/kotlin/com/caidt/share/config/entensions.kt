package com.caidt.share.config

import com.caidt.util.excel.Row


fun Row.readList(column: String): List<String> {
  val string = this.readString(column)
  if (string.isBlank()) return emptyList()
  val s = string.replace("[\\[\\]]".toRegex(), "")
  return s.split(",")
}

fun Row.readBoolean(column: String): Boolean {
  return this.readString(column) == "是"
}