package com.caidt

import akka.actor.ActorRef
import akka.actor.UntypedAbstractActor
import com.caidt.dataContainer.PlayerDC
import com.caidt.proto.ProtoCsMessage
import com.caidt.share.PlayerEnvelope
import com.caidt.share.Tick
import com.caidt.share.entity.PlayerAccountEntity
import com.caidt.share.schedule
import com.google.protobuf.MessageLite
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass


open class PlayerActor : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  enum class State {
    INIT,
    UP,
    LOADING,
    DOWN,
  }

  val playerId: Long get() = playerAccount.playerId

  private var state: State = State.INIT

  private val playerAccount: PlayerAccountEntity get() = playerDC.entity

  val eventBus: EventBus get() = Home.eventBus

  val commonTick: CommonTick get() = Home.commonTick


  override fun onReceive(message: Any?) {
    when (state) {
      State.INIT -> handleOnInit(message)
      State.LOADING -> Unit
      State.UP -> handleOnUp(message)
      State.DOWN -> down() // todo： 需要下线处理
    }
  }

  private fun handleOnInit(message: Any?) {
    when (message) {
      is PlayerEnvelope -> { // 收到的第一个消息，加载基本信息
        loading(message.playerId)
        // todo: 同时处理该消息
      }
    }
  }

  /** 启动加载 */
  private fun loading(playerId: Long) {
    state = State.LOADING
    // 定时
    schedule(Duration.ZERO, Duration.ofSeconds(1L), Tick)
    // todo: 加载少量必要数据，避免浪费
    state = State.UP
    logger.info("player: $playerId is up")
  }

  /** actor down */
  private fun down() {

    disconnect()
  }

  private fun handleOnUp(message: Any?) {
    when (message) {
      Tick -> tick(Instant.now())
      // msg from client
      is ProtoCsMessage.CsMessage -> handleCsMessage(msg = message)
      is MessageLite -> { // 来自其他地方的proto message直接转发到client
        client?.tell(message, self)
      }
    }
  }

  private fun handleCsMessage(msg: ProtoCsMessage.CsMessage) {
    val handler = csMessageHandlers[msg.cmdCase]
    handler?.invoke(this, msg) ?: throw UnsupportedOperationException("not supported")
  }

  private fun tick(now: Instant) {
    // database DataContainer check
    //
    commonTick(now)
  }

  /** 处理player常规定时任务 */
  private fun commonTick(now: Instant) {
    commonTick.tick(this, now)
  }

  private var client: ActorRef? = null

  val isOnline: Boolean get() = client != null

  /** 断开与客户端的连接 */
  private fun disconnect() {
    client = null
    logger.info("player:$playerId was disconnected!")
  }

  /** 回答客户端消息 */
  fun sendToClient(msg: MessageLite) {
    client?.tell(msg, self)
  }

  /** 回答消息 */
  fun answer(msg: Any) {
    sender.tell(msg, this.self)
  }

  val playerDC: PlayerDC get() = getDC(PlayerDC::class)

  fun <E : Any> getDC(clazz: KClass<E>): E {
    TODO()
  }
}
