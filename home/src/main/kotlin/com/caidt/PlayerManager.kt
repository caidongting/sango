package com.caidt

import akka.actor.ActorSystem

class PlayerManager(val actorSystem: ActorSystem) {

  /** 玩家id<playerId, PlayerActor>*/
  val playerIdMap: Map<Long, PlayerActor> = hashMapOf()
  /** <playerName, PlayerActor> */
  val playerNameMap: Map<String, PlayerActor> = hashMapOf()

  fun getOrCreate(playerId: Long) {
  }

}