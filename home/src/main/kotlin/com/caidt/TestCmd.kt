package com.caidt

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass


@Suppress("unused")
enum class TestCmd {

  /**
   * reload all data
   * args: null
   */
  RELOAD {
    override fun exec(player: PlayerActor, args: Array<String>) {
    }
  },
  ;

  abstract fun exec(player: PlayerActor, args: Array<String>)
}


class TestCmdService {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun execCmd(player: PlayerActor, cmd: String, args: Array<String>) {
    val testCmd = requireNotNull(TestCmd.valueOf(cmd)) { "test cmd $cmd not found" }
    try {
      testCmd.exec(player, args)
    } catch (t: Throwable) {
      logger.error("exec cmd error", t)
//            throw t
      t.printStackTrace()
    }
  }

}

fun apply(count: Int) {
  println("apply $count")
}

fun create(number: Int, clazz: KClass<*>): String {
  val params = (1..number).joinToString(", ") { "param$it: ${clazz.simpleName}" }
  return """
    fun apply$number($params) {
        
    }
"""
}
