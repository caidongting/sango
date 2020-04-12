package com.caidt

class PlayerManager {

  /** 玩家id<playerId, PlayerActor>*/
  val playerIdMap: Map<Long, PlayerActor> = hashMapOf()
  /** <playerName, PlayerActor> */
  val playerNameMap: Map<String, PlayerActor> = hashMapOf()



}