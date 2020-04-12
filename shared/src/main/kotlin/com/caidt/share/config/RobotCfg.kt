package com.caidt.share.config

import com.caidt.util.excel.Row

enum class RobotSize {
  SS, S, M, L, LL;
}

enum class Serial {
  UC, 魔神, 圣战士, OO,
}

data class RobotCfg(
  /** uid */
  val uid: Long,
  /** 名字 */
  val name: String,
  /** 显示名字 */
  val displayName: String,
  /** 体积 */
  val size: RobotSize,
  /** 修理费用 */
  val cost: Int,
  /** 系列 */
  val serial: Serial,
  /** hp */
  val hp: Int,
  /** en(能量)  */
  val en: Int,
  /** 移动力 */
  val move: Int,
  /** 运动性 */
  val mobility: Int,
  /** 装甲 */
  val armor: Int,
  /** 限界 */
  val limit: Int,
  /** 武器 */
  val weapons: List<Int>,
  /** 是否有盾 */
  val hasShield: Boolean,
  /** 技能 */
  val skills: List<Int>
) {

  companion object {
    fun readRow(row: Row): RobotCfg {
      val uid = row.readLong("uid")
      val name = row.readString("名字")
      val displayName = row.readString("显示名字")
      val size = RobotSize.valueOf(row.readString("体积"))
      val cost = row.readInt("修理费用")
      val serial = Serial.valueOf(row.readString("系列"))
      val hp = row.readInt("HP")
      val en = row.readInt("EN")
      val move = row.readInt("移动力")
      val mobility = row.readInt("运动性")
      val armor = row.readInt("装甲")
      val limit = row.readInt("限界")
      val weapons = row.readList("武器").map { it.toInt() }
      val hasShield = row.readBoolean("是否有盾")
      val skills = row.readList("技能").map { it.toInt() }
      return RobotCfg(
        uid = uid,
        name = name,
        displayName = displayName,
        size = size,
        cost = cost,
        serial = serial,
        hp = hp,
        en = en,
        move = move,
        mobility = mobility,
        armor = armor,
        limit = limit,
        weapons = weapons,
        hasShield = hasShield,
        skills = skills
      )
    }
  }

}