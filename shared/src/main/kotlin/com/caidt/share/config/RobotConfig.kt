package com.caidt.share.config


import com.caidt.infrastructure.config.ExcelConfig
import com.caidt.util.excel.ExcelFile
import com.caidt.util.notNull


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

  fun getRobot(uid: Long): RobotCfg {
    return notNull(robotMap[uid]) { "cfg not found uid=$uid" }
  }

  fun getRobot(name: String): RobotCfg {
    return notNull(robotNameMap[name]) { "cfg not found name=$name" }
  }

}
