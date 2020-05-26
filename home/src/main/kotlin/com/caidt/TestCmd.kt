package com.caidt

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
