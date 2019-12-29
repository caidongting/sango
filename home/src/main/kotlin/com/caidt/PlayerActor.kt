package com.caidt

import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import com.caidt.infrastructure.PlayerEnvelope
import com.caidt.infrastructure.PlayerId
import com.caidt.infrastructure.Tick
import com.caidt.infrastructure.database.PlayerDC
import com.caidt.infrastructure.entity.PlayerAccountEntity
import com.google.protobuf.MessageLite
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass


open class PlayerActor : UntypedAbstractActor() {

  private val logger: Logger = LoggerFactory.getLogger(javaClass)

  companion object {
    fun props(): Props = Props.create(PlayerActor::class.java) { PlayerActor() }
  }

  enum class State {
    INIT,
    UP,
    LOADING,
    DOWN,
  }

  val playerId: PlayerId get() = playerAccount.playerId

  private var state: State = State.INIT

  private lateinit var playerAccount: PlayerAccountEntity

  val eventBus: EventBus get() = Home.eventBus


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
      is PlayerEnvelope -> {
        state = State.LOADING
        load(message.playerId)
        state = State.UP
      }
    }
  }

  /** 启动加载 */
  private fun load(playerId: PlayerId) {
    //TODO()
    scheduleTick()
    // 加载少量必要数据，避免浪费
    logger.info("player: $playerId is up")
  }

  private var tickCancellable: Cancellable? = null

  private fun scheduleTick() {
    tickCancellable = schedule(Duration.ZERO, Duration.ofSeconds(1L), Tick)
  }

  fun cancelTick() {
    tickCancellable?.cancel()
  }

  private fun handleOnUp(message: Any?) {
    when (message) {
      Tick -> tick(Instant.now())
    }
  }

  private fun tick(now: Instant) {
    // database DataContainer check
    //
    commonTick(now)
  }

  /** 处理player常规定时任务 */
  private fun commonTick(now: Instant) {
    //
  }

  private var client: ActorRef? = null

  val isOnline: Boolean get() = client != null

  fun disconnect() {
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

  /** 向其他玩家发送消息 */
  fun sendToPlayer(playerId: PlayerId, msg: Any) {
    // playerManager or send to shardRegion
  }

  val playerDC: PlayerDC get() = getDC(PlayerDC::class)

  fun <E : Any> getDC(clazz: KClass<E>): E {
    TODO()
  }
}
