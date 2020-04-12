package com.caidt.infrastructure.database

import java.io.Serializable

// 数据库表 容器
abstract class DataContainer<T : IEntity, E : EntityWrapper<T>> {

  private val map: HashMap<Serializable, E> = hashMapOf()

  abstract fun load(entity: T)

  fun wrap(creator: () -> E): E {
    return wrap(creator())
  }

  fun wrap(entity: T, creator: (entity: T) -> E): E {
    return wrap(creator(entity))
  }

  fun wrap(entity: T, clazz: Class<E>) {
    val wrapper = clazz.newInstance()
    wrapper.init(entity)
    wrap(wrapper)
  }

  private fun wrap(wrapper: E): E {
    map[wrapper.primaryKey()] = wrapper
    return wrapper
  }
}