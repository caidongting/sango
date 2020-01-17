package com.caidt

import akka.actor.UntypedAbstractActor

class WorldActor : UntypedAbstractActor() {


  override fun onReceive(message: Any?) {
    when (message) {
      else -> unhandled(message)
    }
  }
}