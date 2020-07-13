package com.caidt.util

import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class Factor(var numerator: Long, var denominator: Long) {

  // 自我归约
  fun reduce(): Factor {
    if (numerator.rem(denominator) == 0L) {
      val num = numerator / denominator
      return Factor(num, 1)
    } else if (denominator.rem(numerator) == 0L) {
      val num = denominator / numerator
      return Factor(1, num)
    }
    return this
  }

  fun reduce(factor: Factor) {
    if (factor.numerator == denominator) {
      denominator = 1L
      factor.numerator = 1L
    }
    if (factor.denominator == numerator) {
      numerator = 1L
      factor.denominator = 1L
    }
  }

  val isEmpty: Boolean get() = numerator == 1L && denominator == 1L

}

/**
 * 整数乘除法计算器
 * 处理整数的乘除，避免溢出，精度缺失，计算错误等问题
 */
class Multiplier private constructor(private val value: Long) {

  private val factorList: MutableList<Factor> = LinkedList()

  companion object {
    fun create(value: Long = 1L): Multiplier = Multiplier(value)
  }

  //region ================ underlying ========================================
  operator fun times(value: Long): Multiplier {
    return this * Factor(value, 1L)
  }

  operator fun div(value: Long): Multiplier {
    require(value != 0L) { "denominator must not be zero" }
    return this * Factor(1L, value)
  }

  operator fun times(pair: Pair<Int, Int>): Multiplier {
    val (numerator, denominator) = pair
    return this * Factor(numerator.toLong(), denominator.toLong())
  }

  operator fun times(factor: Factor): Multiplier {
    require(factor.denominator != 0L) { "denominator must not be zero" }
    val f = factor.reduce()
    factorList.forEach { it.reduce(f) }
    if (!f.isEmpty) factorList.add(f)
    return this
  }

  fun calculate(): Double {
    factorList.removeIf { it.isEmpty }
    val numerator = factorList.map { it.numerator }.fold(value) { acc, l -> Math.multiplyExact(acc, l) }
    val denominator = factorList.map { it.denominator }.fold(1L) { acc, l -> Math.multiplyExact(acc, l) }
    return 1.0 * numerator / denominator
  }
  //endregion

  //region ================ extensions ========================================
  operator fun times(value: Int): Multiplier {
    return times(value.toLong())
  }

  operator fun div(value: Int): Multiplier {
    return div(value.toLong())
  }

  fun times(numerator: Long, denominator: Long): Multiplier {
    return this * Factor(numerator, denominator)
  }

  fun div(numerator: Long, denominator: Long): Multiplier {
    return this * Factor(denominator, numerator)
  }

  fun onePlus(numerator: Long, denominator: Long): Multiplier {
    return times(denominator + numerator, denominator)
  }

  fun oneMinus(numerator: Long, denominator: Long): Multiplier {
    return times(denominator - numerator, denominator)
  }

  fun roundToInt(): Int {
    return calculate().roundToInt()
  }

  fun roundToLong(): Long {
    return calculate().roundToLong()
  }
  //endregion
}
