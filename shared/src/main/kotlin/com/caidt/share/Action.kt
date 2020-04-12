package com.caidt.share

/** player 主动导致的操作，需要判断 */
enum class Action(val id: Int, val msg: String) {
  LOGIN(1, "login"),
  RELOGIN(2, "relogin"),


  ;
}
