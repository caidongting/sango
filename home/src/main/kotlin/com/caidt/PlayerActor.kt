package com.caidt

import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.UntypedAbstractActor
import com.caidt.dataContainer.PlayerDC
import com.caidt.memory.DataContainer
import com.caidt.memory.DataContainerManager
import com.caidt.proto.ProtoCsMessage
import com.caidt.share.Disconnect
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

  val worldId: Long get() = playerAccount.worldId

  private var state: State = State.INIT

  private val playerAccount: PlayerAccountEntity get() = playerDC.entity

  val eventBus: EventBus by lazy { EventBus(context.system) }

  val commonTick: CommonTick by lazy { CommonTick(context.system) }


  override fun onReceive(message: Any?) {
    when (state) {
      State.INIT -> handleOnInit(message)
      State.LOADING -> Unit
      State.UP -> handleOnUp(message)
      State.DOWN -> down()
    }
  }

  private fun handleOnInit(message: Any?) {
    when (message) {
      is PlayerEnvelope -> { // 收到的第一个[PlayerEnvelope]消息，加载基本信息
        loading(message.playerId)
        // todo: 同时处理该消息
      }
    }
  }

  /** 启动加载 */
  private fun loading(playerId: Long) {
    state = State.LOADING
    // todo: 加载少量必要数据，避免浪费
    // 定时
    scheduleTick()
    state = State.UP
    logger.info("player: $playerId is up")
  }

  private var cancelTick: Cancellable? = null

  private fun scheduleTick() {
    cancelTick = schedule(Duration.ZERO, Duration.ofSeconds(1L), Tick)
  }

  private fun cancelTick() {
    cancelTick?.cancel()
    cancelTick = null
  }

  /** 下线处理 */
  private fun down() {
    // todo: 钝化 ？什么意思？清空状态，但不销毁actor?
    cancelTick()
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

  /** 处理player常规定时任务 */
  private fun tick(now: Instant) {
    // database DataContainer check
    //
    commonTick.tick(this, now)
  }

  private var client: ActorRef? = null

  val isOnline: Boolean get() = client != null

  /** 断开与客户端的连接 */
  private fun disconnect() {
    client?.tell(Disconnect, self)
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

  inline fun <reified E : DataContainer<*, *>> getDC(clazz: KClass<E>): E {
    return DataContainerManager.getOrLoad(playerId, clazz)
  }

  fun require(dc: DataContainer<*, *>) {

  }

  fun require(dc1: DataContainer<*, *>, dc2: DataContainer<*, *>) {

  }
}
