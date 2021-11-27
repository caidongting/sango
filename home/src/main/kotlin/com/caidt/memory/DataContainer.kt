package com.caidt.memory

import com.caidt.infrastructure.database.EntityWrapper
import com.caidt.infrastructure.database.EntityWrapperManager
import com.caidt.infrastructure.database.IEntity
import java.io.Serializable
import java.lang.reflect.ParameterizedType

// 数据库表 容器
abstract class DataContainer<T : IEntity, E : EntityWrapper<T>> {

  private val map: HashMap<Serializable, E> = hashMapOf()

  private lateinit var manager: EntityWrapperManager

  private val clazz: Class<T>

  init {
    val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
    clazz = genericSuperclass.actualTypeArguments[0] as Class<T>
  }

  abstract fun load(entity: T)

  fun wrap(creator: () -> E): E {
    return wrap(creator())
  }

  fun wrap(entity: T, creator: (entity: T) -> E): E {
    return wrap(creator(entity))
  }

//  // 适用于只有一个entity参数的
//  fun wrap(entity: T, clazz: Class<E>) {
//    val wrapper = clazz.getConstructor().newInstance()
//    wrap(wrapper)
//  }

  private fun wrap(wrapper: E): E {
    map[wrapper.primaryKey()] = wrapper
    manager.createTracker(clazz, wrapper)
    return wrapper
  }

  fun save(wrapper: E): E {
    map[wrapper.primaryKey()] = wrapper
    manager.save(clazz, wrapper)
    return wrapper
  }

  fun delete(wrapper: E) {
    map.remove(wrapper.primaryKey())
    manager.delete(clazz, wrapper)
  }
}