package com.caidt.memory

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object DataContainerManager {

  // 容器合集
  private val containerMap: MutableMap<Long, MutableMap<KClass<*>, DataContainer<*, *>>> = ConcurrentHashMap()

  private val dbRead: Executor = Executor { }
  private val dbWrite: Executor = Executors.newSingleThreadExecutor()
  private val exec: HashMap<String, Executor> = hashMapOf()

  fun <T : DataContainer<*, *>> getOrLoad(uid: Long, clazz: KClass<T>): T {
    val map = containerMap.computeIfAbsent(uid) { mutableMapOf() }
    return map.computeIfAbsent(clazz) {
      val instance = clazz.createInstance()
      // todo: load data
      //  切换到IO线程读取数据
      instance
    } as T
  }

}
