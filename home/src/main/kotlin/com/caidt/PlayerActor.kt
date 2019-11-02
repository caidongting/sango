package com.caidt

import akka.actor.UntypedAbstractActor
import com.caidt.infrastructure.Ok
import com.caidt.infrastructure.PlayerId
import com.caidt.infrastructure.PlayerMessage
import com.caidt.infrastructure.Tick
import com.caidt.infrastructure.entity.PlayerAccountEntity
import com.google.protobuf.MessageLite
import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant


open class PlayerActor : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  enum class State {
    INIT,
    UP,
    LOADING,
    DOWN,
  }

  val playerId: PlayerId get() = playerAccount.playerId

  private var state: State = State.INIT

  private lateinit var playerAccount: PlayerAccountEntity


  override fun onReceive(message: Any?) {
    when (state) {
      State.INIT -> handleOnInit(message)
      State.UP -> handleOnUp(message)
      State.LOADING -> {
      }
      State.DOWN -> {
      }
    }
  }

  private fun handleOnInit(message: Any?) {
    when (message) {
      is MessageLite -> {
      }
      is PlayerMessage -> {
        state = State.LOADING
        load(message.playerId)
        state = State.UP
      }
    }
  }

  /** 启动加载 */
  private fun load(playerId: PlayerId) {
    //TODO()
    logger.info("player: $playerId is up")
  }

  private fun handleOnUp(message: Any?) {
    when (message) {
      Tick -> commonTick(Instant.now())
    }
  }

  /** 处理player常规定时任务 */
  private fun commonTick(now: Instant) {
    //
  }

  private var client: Channel? = null

  /** 回答客户端消息 */
  fun sendToClient(msg: MessageLite) {
    client?.writeAndFlush(msg)
  }

  fun answerOk() {
    answer(Ok)
  }

  /** 发送消息到其他 */
  fun answer(msg: Any) {
    sender.tell(msg, this.self)
  }

  /** 像其他玩家发送消息 */
  fun sendToPlayer(msg: Any) {

  }
}
