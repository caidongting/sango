package com.caidt.infrastructure.database

import akka.actor.UntypedAbstractActor
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.reflect.KClass

object DataContainerManager {

  private val containerMap: Map<KClass<*>, DataContainer<*, *>> = hashMapOf()

  private val dbRead: Executor = Executor { }
  private val dbWrite: Executor = Executors.newSingleThreadExecutor()
  private val exec: HashMap<String, Executor> = hashMapOf()

  fun <T : Any> getOrLoad(clazz: KClass<T>): T {
    TODO()
  }

}

class Worker : UntypedAbstractActor() {
  override fun onReceive(message: Any?) {
    when (message) {
      is Runnable -> message.run()
    }
  }
}