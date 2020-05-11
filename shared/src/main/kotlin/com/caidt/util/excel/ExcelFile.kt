package com.caidt.util.excel

import com.caidt.util.excel.PoiUtils.getDateValue
import com.caidt.util.excel.PoiUtils.getDoubleValue
import com.caidt.util.excel.PoiUtils.getFloatValue
import com.caidt.util.excel.PoiUtils.getIntValue
import com.caidt.util.excel.PoiUtils.getStringValue
import com.caidt.util.excel.PoiUtils.isEmpty
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap

enum class PathType {
  /** classpath路径 */
  CLASSPATH,

  /** 绝对路径 */
  ABSOLUTE;
}

class ExcelFile private constructor(
  private val filePath: String,
  private val pathType: PathType
) : Closeable {

  private var workbook: XSSFWorkbook? = null
  private var input: InputStream? = null

  /**
   * 打开文件
   */
  fun open(): ExcelFile {
    when (pathType) {
      PathType.CLASSPATH -> openClasspath()
      PathType.ABSOLUTE -> openAbsolute()
    }
    return this
  }

  private fun openClasspath() {
    try {
      val url = javaClass.classLoader.getResource(filePath)
      val conn = url.openConnection()
      conn.useCaches = false
      input = conn.getInputStream()
      workbook = XSSFWorkbook(input)
    } catch (e: Exception) {
      close()
      throw RuntimeException("Excel open fail: $filePath", e)
    }
  }

  private fun openAbsolute() {
    try {
      input = FileInputStream(filePath)
      workbook = XSSFWorkbook(input)
    } catch (e: Exception) {
      close()
      throw RuntimeException("Excel open fail: $filePath", e)
    }
  }

  /**
   * 关闭文件
   */
  override fun close() {
    if (workbook != null) {
      workbook = null
    }
    if (input != null) {
      try {
        input?.close()
      } catch (e: Exception) {
      }
    }
  }

  fun foreachRow(sheetIndex: Int, parser: (Row) -> Unit) {
    val sheet = getSheetAt(sheetIndex)
    foreachRow(sheet, parser)
  }

  fun foreachRow(sheetName: String, parser: (Row) -> Unit) {
    val sheet = getSheet(sheetName)
    foreachRow(sheet, parser)
  }

  fun foreachRow(sheet: XSSFSheet, parser: (Row) -> Unit) {
    val columnBiMap = makeColumnMap(sheet)
    val lastRowNum = sheet.lastRowNum // 0-based
    var exception: Throwable? = null
    for (i in 1..lastRowNum) {
      try {
        val xssfRow = sheet.getRow(i)
        if (isEmpty(xssfRow)) continue

        val row = Row(xssfRow, columnBiMap)
        parser.invoke(row)
      } catch (e: Exception) {
        e.printStackTrace()

        if (exception == null) {
          exception = RuntimeException()
        }
      }
    }
    exception?.let { throw it }
  }

  /**
   * 读取表头
   * 返回 列名字到列索引的映射 map<名字，索引>
   */
  private fun makeColumnMap(sheet: XSSFSheet): Map<String, Int> {
    val headerMap: MutableMap<String, Int> = HashMap()
    val xssfRow = sheet.getRow(0)
    for (i in 0 until xssfRow.lastCellNum) {
      val cell = xssfRow.getCell(i)
      val name = getStringValue(cell)
      if (name.isNotEmpty()) {
        headerMap[name.trim { it <= ' ' }] = i
      }
    }
    return headerMap
  }

  fun hasSheet(sheetName: String): Boolean {
    return workbook?.getSheet(sheetName) != null
  }

  fun getSheet(sheetName: String): XSSFSheet {
    return workbook?.getSheet(sheetName)
      ?: throw IllegalArgumentException("cannot find sheet $sheetName in $filePath")
  }

  fun getSheetAt(sheetIndex: Int): XSSFSheet {
    return workbook?.getSheetAt(sheetIndex)
      ?: throw IllegalArgumentException("cannot find sheet index $sheetIndex in $filePath")
  }

  companion object {
    /**
     * 解析excel，路径类型默认为classpath
     */
    @JvmStatic
    fun parse(filePath: String, parser: (ExcelFile) -> Unit) {
      parse(filePath, PathType.CLASSPATH, parser)
    }

    /**
     * 解析excel，指定路径类型
     */
    @JvmStatic
    fun parse(filePath: String, pathType: PathType, parser: (ExcelFile) -> Unit) {
      ExcelFile(filePath, pathType).open().use { excel -> parser.invoke(excel) }
    }
  }

}

/**
 * excel的一行
 *
 * @author liul
 * @since 2013年9月2日
 */
class Row(
  private val xssfRow: XSSFRow,
  private val columnBiMap: Map<String, Int>
) {

  private var curCellIndex = 0

  fun hasColumn(column: String?): Boolean {
    return columnBiMap.containsKey(column)
  }

  private fun getCellByColumnName(column: String): XSSFCell? {
    curCellIndex = columnBiMap[column] ?: throw RuntimeException("找不到列: $column")
    return xssfRow.getCell(curCellIndex)
  }

  fun readInt(column: String): Int {
    val cell = getCellByColumnName(column)
    return getIntValue(cell)
  }

  fun readLong(column: String): Long {
    return readString(column).toLong()
  }

  fun readDouble(column: String): Double {
    val cell = getCellByColumnName(column)
    return getDoubleValue(cell)
  }

  fun readFloat(column: String): Float {
    val cell = getCellByColumnName(column)
    return getFloatValue(cell)
  }

  fun readString(column: String): String {
    val cell = getCellByColumnName(column)
    return getStringValue(cell)
  }

  fun readDate(column: String): Date? {
    val cell = getCellByColumnName(column)
    return getDateValue(cell)
  }

}