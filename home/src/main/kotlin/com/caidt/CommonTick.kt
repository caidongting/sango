package com.caidt

import akka.actor.ActorContext
import akka.actor.ActorRef
import com.caidt.share.TickDuration
import com.caidt.share.TickTimer
import com.caidt.share.Worker
import java.time.Instant


class CommonTick(context: ActorContext) {

  private val executor: ActorRef = context.actorOf(Worker.props())

  private val common: TickDuration = TickDuration("commonTick", executor, 10)

  private val testTicker: TickTimer = TickTimer(executor, 5)


  fun tick(player: PlayerActor, now: Instant) {
    common.tick("resource", 1L) {
      playerService.calcResource(player, now)
    }

    testTicker.tick {

    }

  }

}