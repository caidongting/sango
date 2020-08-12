package com.caidt.infrastructure.config

import com.caidt.infrastructure.EXCEL_CONFIG_DIR
import com.caidt.share.config.ItemConfig
import com.caidt.share.config.RobotConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ExcelConfigs {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  lateinit var manager: ExcelManager
    private set

  fun init() {
    val manager = ExcelManager()
    manager.loadAll(EXCEL_CONFIG_DIR)
    manager.afterLoadAll()
    ExcelConfigs.manager = manager

    logger.info("excel config load success!!")
  }

  /** 替换当前配置 */
  fun replaceManager(manager: ExcelManager) {
    synchronized(this) {
      ExcelConfigs.manager = manager
    }
  }

  fun <T : ExcelConfig> getConfig(clazz: Class<T>): T = manager.getConfig(clazz)


  val robotConfig: RobotConfig get() = getConfig(RobotConfig::class.java)

  val itemConfig: ItemConfig get() = getConfig(ItemConfig::class.java)

}