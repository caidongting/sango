package com.caidt

import org.slf4j.LoggerFactory

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