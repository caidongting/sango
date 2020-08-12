package com.caidt

import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.UntypedAbstractActor
import akka.japi.Procedure
import com.caidt.dataContainer.PlayerDC
import com.caidt.infrastructure.GameException
import com.caidt.memory.DataContainer
import com.caidt.memory.DataContainerManager
import com.caidt.message.clientMessageHandlers
import com.caidt.message.innerMessageHandlers
import com.caidt.proto.ProtoCommon
import com.caidt.proto.ProtoCsMessage
import com.caidt.share.*
import com.caidt.share.entity.PlayerAccountEntity
import com.google.protobuf.MessageLite
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
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

  val eventBus: EventBus by lazy { EventBus(context) }

  val commonTick: CommonTick by lazy { CommonTick(context) }

  val s = Procedure<Any> { message ->
    when (message) {
      is PlayerEnvelope -> handleOnUp(message)
    }
  }

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
      is PlayerEnvelope -> handleEnvelope(message.payload) // envelop 即通用的来源可以来自任何地方
      /**
       * 内部直接应答消息 即通过[ActorRef]发送
       * todo: 异步请求如何传递消息过去？？？
       *  resolve：通过[akka.pattern.Patterns.ask]来实现，underlying 是启动一个临时actor实现发送和处理返回消息
       * @see [java.util.concurrent.CompletionStage] [java.util.concurrent.CompletableFuture]等，需要深入再了解
       */
//      is PlayerMessage -> Unit
      Disconnect -> state = State.DOWN
      else -> Unit
    }
  }

  private fun handleEnvelope(payload: Any) {
    when (payload) {
      // from client
      is ProtoCsMessage.CsMessage -> handleCsMessage(payload)
      // from server (self or other)
      is PlayerMessage -> innerMessageHandlers(this, payload)
      // 来自其他地方的proto message直接转发到client
      is MessageLite -> sendToClient(payload)
    }
  }


  private fun handleInnerMessage() {

  }

  private fun handleCsMessage(msg: ProtoCsMessage.CsMessage) {
    try {
      val handler = clientMessageHandlers[msg.cmdCase]
      handler?.invoke(this, msg) ?: throw UnsupportedOperationException("not supported")
    } catch (e: GameException) {
      sendToClientError(ProtoCommon.Reason.game, e.message)
      logger.error("", e)
    } catch (e: RuntimeException) {
      sendToClientError(ProtoCommon.Reason.common, e.message)
      logger.error("handle client message error", e)
    }
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
    // todo: ??? 是否需要组装成 ScMessage 以及如何组装？？？
    client?.tell(msg, self)
  }

  /** 返还客户端的错误信息 */
  private fun sendToClientError(reason: ProtoCommon.Reason, message: String? = null) {
    ProtoCommon.Error.newBuilder().let {
      it.reason = reason
      it.msg = message
      sendToClient(it.build())
    }
  }

  /** 回答消息 (一般为内部应答，应答客户端消息用[sendToClient]) */
  fun answer(msg: Any) {
    sender.tell(msg, this.self)
  }

  val playerDC: PlayerDC get() = getDC(PlayerDC::class)

  inline fun <reified E : DataContainer<*, *>> getDC(clazz: KClass<E>): E {
    return DataContainerManager.getOrLoad(playerId, clazz)
  }

  /** 用于加载需要的[DataContainer] */
  fun require(c1: Class<DataContainer<*, *>>) {
//    require(PlayerDC::class)
  }

  fun <T> patternCS(): CompletionStage<T> = CompletableFuture()

}
