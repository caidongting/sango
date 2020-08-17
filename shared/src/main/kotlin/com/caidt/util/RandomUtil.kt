@file:Suppress("unused")

package com.caidt.util

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.reflect.KProperty1

object RandomUtil {

  /**
   * 从[origin]中随机取出[n]个元素
   */
  fun <E> select(origin: Collection<E>, n: Int): List<E> {
    require(n >= 0) { "n=$n must above zero" }
    if (origin.isEmpty()) return emptyList()

    val size = origin.size
    if (size <= n) return ArrayList(origin)

    val result: MutableList<E> = ArrayList(origin)
    repeat(n) { i ->
      val j = ThreadLocalRandom.current().nextInt(i, size)
      Collections.swap(result, i, j)
    }
    return result.subList(0, n)
  }

  /**
   * 从[list]中随机取出[n]个元素
   */
  fun <E> select(list: MutableList<E>, n: Int): List<E> {
    require(n >= 0) { "n=$n must above zero" }
    if (list.isEmpty()) return emptyList()

    if (list.size <= n) return list

    list.shuffle()
    return list.subList(0, n)
  }

  /**
   * 从[origin]中根据权重随机一个元素
   */
  fun <E> select(origin: Collection<E>, weight: KProperty1<E, Int>): E {
    require(origin.isNotEmpty()) { "origin collection is empty" }

    val totalWeight = origin.sumBy(weight)
    val random = ThreadLocalRandom.current().nextInt(totalWeight)
    var sum = 0
    for (e in origin) {
      sum += weight.get(e)
      if (random < sum) {
        return e
      }
    }
    throw IllegalStateException("unreachable code is reached")
  }

  /**
   * 从[start,end)中随机出一个Int
   */
  fun between(start: Int, end: Int): Int {
    require(start < end) { "start=$start must < end=$end" }
    return ThreadLocalRandom.current().nextInt(start, end)
  }

  fun isHit(percent: Int, base: Int): Boolean {
    if (percent >= base) return true
    return percent >= ThreadLocalRandom.current().nextInt(base) + 1
  }

}

