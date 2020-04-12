package com.caidt.infrastructure.config

import com.caidt.util.scanPackage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** 被标记的[ExcelConfig]不会加载 */
annotation class DoNotLoad

abstract class ExcelConfig {

  private lateinit var manager: ExcelManager

  internal fun setManager(manager: ExcelManager) {
    this.manager = manager
  }

  /**
   * 用于在[afterLoadAll]阶段对其他[ExcelConfig]的依赖，若用于[load]阶段，需要注意加载关系
   */
  fun <T : ExcelConfig> getConfig(clazz: Class<T>): T {
    return manager.getConfig(clazz)
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

  fun loadAll(packageName: String) {
    val classes = scanPackage(packageName) {
      !it.isAnnotationPresent(DoNotLoad::class.java) &&
          ExcelConfig::class.java.isAssignableFrom(it) && !it.kotlin.isAbstract
    }
    // todo: 个别[ExcelConfig]之间有依赖关系，需要设置加载顺序
    @Suppress("UNCHECKED_CAST")
    classes.forEach { clazz -> reload(clazz as Class<out ExcelConfig>) }
  }

  private fun reload(clazz: Class<out ExcelConfig>) {
    try {
      val config = clazz.newInstance().also {
        it.setManager(this)
        it.load()
        it.afterLoad()
        logger.debug("loaded excelConfig: ${clazz.simpleName}")
      }
      map[clazz] = config
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


