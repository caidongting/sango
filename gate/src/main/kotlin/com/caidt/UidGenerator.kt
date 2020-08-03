package com.caidt

import akka.actor.Props
import akka.actor.UntypedAbstractActor

class UidGenerator : UntypedAbstractActor() {

  companion object {
    fun props(): Props {
      return Props.create(UidGenerator::class.java)
    }
  }

  override fun onReceive(message: Any?) {
    TODO("Not yet implemented")
  }

}