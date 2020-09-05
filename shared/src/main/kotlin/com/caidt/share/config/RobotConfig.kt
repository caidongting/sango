package com.caidt.share.config


import com.caidt.infrastructure.config.DoNotLoad
import com.caidt.infrastructure.config.ExcelConfig
import com.caidt.util.excel.ExcelFile

@DoNotLoad
class RobotConfig : ExcelConfig() {

  /** 机体配置 <uid, RobotCfg> */
  private val robotMap: HashMap<Long, RobotCfg> = hashMapOf()

  /** 机体配置 <name, RobotCfg> */
  private val robotNameMap: HashMap<String, RobotCfg> = hashMapOf()

  override fun load() {
    ExcelFile.parse("excel/robot.xlsx") { excel ->
      excel.foreachRow("机体") { row ->
        val robot = RobotCfg.readRow(row)
        robotMap[robot.uid] = robot
      }

    }
  }

  override fun afterLoadAll() {
  }

  operator fun get(uid: Long): RobotCfg {
    return requireNotNull(robotMap[uid]) { "cfg not found uid=$uid" }
  }

  operator fun get(name: String): RobotCfg {
    return requireNotNull(robotNameMap[name]) { "cfg not found name=$name" }
  }

}
