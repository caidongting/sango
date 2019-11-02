package com.caidt.infrastructure

import com.alibaba.excel.EasyExcel

annotation class DoNotLoad

abstract class ExcelConfig {

  abstract fun readExcel()

  abstract fun afterLoadAll()
}

class CommonConfig : ExcelConfig() {

  override fun readExcel() {
    val read = EasyExcel.read("text.xlsx")
    read.sheet("1").doRead()
    read.sheet("").doRead()
  }

  override fun afterLoadAll() {
  }

}