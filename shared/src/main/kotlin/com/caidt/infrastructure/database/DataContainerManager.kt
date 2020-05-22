package com.caidt.infrastructure.database

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.reflect.KClass

object DataContainerManager {

  // 容器合集
  private val containerMap: MutableMap<KClass<*>, DataContainer<*, *>> = mutableMapOf()

  private val dbRead: Executor = Executor { }
  private val dbWrite: Executor = Executors.newSingleThreadExecutor()
  private val exec: HashMap<String, Executor> = hashMapOf()

  fun <T : DataContainer<*, *>> getOrLoad(clazz: KClass<T>): T {
    TODO()
  }

}
