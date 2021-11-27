package com.caidt.util.excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import java.security.InvalidParameterException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

/**
 * Utils to operate excels with POI
 *
 * @author Playboy
 * @since 2010-12-22
 */
object PoiUtils {

  private val FMT_NUMBER: NumberFormat = DecimalFormat("0.####")

  fun getIntValue(cell: XSSFCell?): Int {
    if (cell == null || cell.toString().trim { it <= ' ' }.isEmpty()) {
      return 0
    }
    return if (cell.cellType == CellType.FORMULA)
      cell.numericCellValue.toInt()
    else
      cell.toString().toDouble().toInt()
  }

  fun getLongValue(cell: XSSFCell?): Long {
    if (cell == null || cell.toString().trim { it <= ' ' }.isEmpty()) {
      return 0L
    }
    return if (cell.cellType == CellType.FORMULA)
      cell.numericCellValue.toLong()
    else
      cell.rawValue.toLong()
  }

  fun getShortValue(cell: XSSFCell?): Short {
    return if (cell == null || cell.toString().isEmpty()) {
      0
    } else cell.toString().toDouble().toInt().toShort()
  }

  fun getByteValue(cell: XSSFCell?): Byte {
    return if (cell == null || cell.toString().isEmpty()) {
      0
    } else cell.toString().toDouble().toInt().toByte()
  }

  fun getDoubleValue(cell: XSSFCell?): Double {
    return cell?.numericCellValue ?: 0.0
  }

  fun getDateValue(cell: XSSFCell?): Date? {
    return if (cell != null && cell.toString().isNotEmpty()) {
      cell.dateCellValue
    } else null
  }

  fun getStringValue(cell: XSSFCell?): String {
    if (cell == null) {
      return ""
    }
    return when (cell.cellType) {
      CellType.STRING -> cell.toString().trim { it <= ' ' }
      CellType.NUMERIC -> {
        val str = FMT_NUMBER.format(cell.numericCellValue)
        if (str.endsWith(".0")) {
          str.substring(0, str.length - 2)
        } else str
      }
      CellType.FORMULA -> cell.richStringCellValue.string
      else -> cell.toString().trim { it <= ' ' }
    }
  }

  fun getFloatValue(cell: XSSFCell?): Float {
    if (cell == null) return 0.0f
    if (cell.cellType == CellType.FORMULA) {
      return cell.numericCellValue.toFloat()
    }
    val cellStr = cell.toString()
    if (cellStr.isEmpty()) return 0.0f
    if (cellStr == "0%") return 0f
    return if (cellStr.indexOf('%') > 0)
      getPercentum(cell)
    else
      cellStr.toFloat()
  }

  fun isEmpty(row: XSSFRow?): Boolean {
    if (row == null) {
      return true
    }
    val cell = row.getCell(0)
    return cell == null || cell.toString().isEmpty()
  }

  private fun getPercentum(cell: XSSFCell?): Float {
    if (cell == null || cell.toString().isEmpty()) {
      return 0.0f
    }
    try {
      val parse = NumberFormat.getPercentInstance().parse(cell.toString())
      return parse.toFloat()
    } catch (e: ParseException) {
      throw InvalidParameterException("无法将此格式转换成小数：$cell")
    }
  }

  /**
   * 将列索引转成由由A-Z表示的列名,支持范围A~YZ
   *
   * @param columnIndex 有效范围0~26^2-1(675)
   * @return 列名
   */
  fun toExcelColumnName(columnIndex: Int): String {
    if (columnIndex > 26 * 26 - 1) {
      throw IndexOutOfBoundsException(columnIndex.toString())
    }
    val _26RadixNum = columnIndex.toString(26)
    val excelColName = StringBuilder()
    if (_26RadixNum.length == 1) {
      excelColName.append(convertAAs0(_26RadixNum[0]))
    } else if (_26RadixNum.length == 2) {
      excelColName.append(convertAAs1(_26RadixNum[0]))
      for (i in 1 until _26RadixNum.length) {
        excelColName.append(convertAAs0(_26RadixNum[i]))
      }
    }
    return excelColName.toString()
  }

  private fun convertAAs1(c: Char): Char {
    return when {
      c in '1'..'9' -> (c.code + ('A' - '1')).toChar() // 1~9 -> A->I
      c != '0' -> (c.code - ('a' - 'J')).toChar() // a~p -> J~Z
      else -> c
    }
  }

  private fun convertAAs0(c: Char): Char {
    return if (c in '0'..'9')
      (c.code + ('A' - '0')).toChar() // 0~9 -> A->J
    else
      (c.code - ('a' - 'K')).toChar() // a~p -> K~Z
  }
}