package com.caidt.memory

import com.caidt.PlayerActor
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object DataContainerManager {

  // 容器合集
  private val containerMap: MutableMap<Long, MutableMap<KClass<*>, DataContainer<*, *>>> = ConcurrentHashMap()

  private val dbRead: Executor = Executor { } // 通过统一调度器来协调，Q:是否需要每个player 创建一个
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


  /******************************  database load  ***********************************/
  /** 用于加载需要的[DataContainer] */
  inline fun <reified T : DataContainer<*, *>> PlayerActor.require(c1: KClass<T>): CompletableFuture<T> {
    return CompletableFuture.supplyAsync {
      getDC(c1)
    }
  }

  /** 最多支持 18 or 19 个参数？？？，不使用varargs 因为参数展开的效率更高 */
  fun require(c1: KClass<DataContainer<*, *>>, c2: KClass<DataContainer<*, *>>) {
    CompletableFuture.supplyAsync { }
  }

  inline fun <reified A : DataContainer<*, *>, reified B : DataContainer<*, *>, reified C : DataContainer<*, *>>
      PlayerActor.require(c1: KClass<A>, c2: KClass<B>, c3: KClass<C>): CompletableFuture<Result<A, B, C>> {
    val executor = Executor { getDC(c1) }
    Executor { getDC(c2) }
    Executor { getDC(c3) }
//    return CompletableFuture.supplyAsync {
//
//    }
    TODO()
  }


  inline fun <reified T : DataContainer<*, *>> executor(clazz: KClass<T>): T {
    return getOrLoad(1L, clazz)
  }

  data class Result<A, B, C>(val a: A, val b: B, val c: C)
}
