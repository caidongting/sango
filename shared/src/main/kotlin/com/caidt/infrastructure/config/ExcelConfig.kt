package com.caidt.infrastructure.config

import com.caidt.share.config.ItemConfig
import com.caidt.share.config.RobotConfig
import com.caidt.util.scanPackage
import com.google.common.base.Stopwatch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/** 被标记的[ExcelConfig]不会加载 */
annotation class DoNotLoad

abstract class ExcelConfig {

  /**
   * 用于在[afterLoadAll]阶段对其他[ExcelConfig]的依赖，若用于[load]阶段，需要注意加载关系
   */
  lateinit var manager: ExcelManager
    private set

  internal fun setManager(manager: ExcelManager) {
    this.manager = manager
  }

  abstract fun load()

  open fun afterLoad() {}

  abstract fun afterLoadAll()
}

class ExcelManager {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  private val map: MutableMap<Class<out ExcelConfig>, ExcelConfig> = hashMapOf()

  fun <T : ExcelConfig> getConfig(clazz: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return map[clazz] as T? ?: throw IllegalStateException("ExcelConfig not found: ${clazz.simpleName}")
  }

  companion object {
    /** 优先加载的excel */
    private val BEFORE_LOAD_EXCEL: List<Class<out ExcelConfig>> = listOf(
      ItemConfig::class.java
    )

    /** 加载依赖，在加载前先加载依赖 */
    private val DEPEND_EXCEL: List<Pair<Class<out ExcelConfig>, Class<out ExcelConfig>>> = listOf(
      RobotConfig::class.java to ItemConfig::class.java
    )
  }

  fun loadAll(packageName: String) {
    val stopwatch = Stopwatch.createStarted()
    val classes = scanAllExcelConfig(packageName)
    stopwatch.stop()
    logger.info("scan all excel cost: ${stopwatch.elapsed(TimeUnit.MILLISECONDS)} ms")

    stopwatch.reset()
    // 有些基础的excel需要优先加载
    BEFORE_LOAD_EXCEL.forEach { reload(it) }

    for (clazz in classes) {
      if (clazz in map) continue // already load

      // 个别[ExcelConfig]之间有依赖关系，需要先加载依赖的excel
      DEPEND_EXCEL.filter { it.first === clazz && it.second !in map }
        .forEach { reload(it.second) }
      @Suppress("UNCHECKED_CAST")
      reload(clazz as Class<out ExcelConfig>)
    }
    logger.info("load all excel cost: ${stopwatch.elapsed(TimeUnit.MILLISECONDS)} ms")
  }

  private fun reload(clazz: Class<out ExcelConfig>) {
    try {
      clazz.newInstance().also {
        it.setManager(this)
        it.load()
        it.afterLoad()
        logger.debug("loaded excelConfig: ${clazz.simpleName}")
        map[clazz] = it
      }
    } catch (e: Exception) {
      throw RuntimeException("load config: ${clazz.simpleName} failed", e)
    }
  }

  fun afterLoadAll() {
    map.forEach { (clazz, config) ->
      config.afterLoadAll()
      logger.debug("after load all excelConfig: ${clazz.simpleName}")
    }
  }

}

internal fun scanAllExcelConfig(packageName: String): List<Class<*>> {
  return scanPackage(packageName) {
    ExcelConfig::class.java.isAssignableFrom(it) &&
        it != ExcelConfig::class.java &&
        !it.isAnnotationPresent(DoNotLoad::class.java)
  }
}

