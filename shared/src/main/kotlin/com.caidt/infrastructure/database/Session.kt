package com.caidt.infrastructure.database

import com.caidt.infrastructure.entity.PlayerAccountEntity
import org.hibernate.Session
import org.hibernate.SessionFactory
import java.io.Serializable
import java.time.Duration

interface IEntity {
  fun primaryKey(): Serializable
}


class Session(private val sessionFactory: SessionFactory) {

  fun exec(task: (session: Session) -> Any?): Any? {
    val session = sessionFactory.openSession()
    val transaction = session.beginTransaction()
    try {
      val result = task(session)
      transaction.commit()
      return result
    } catch (e: Exception) {
      transaction.rollback()
      throw RuntimeException(e)
    } finally {
      session.close()
    }
  }

  fun <T : IEntity> read(clazz: Class<T>, pk: Serializable): T {
    @Suppress("UNCHECKED_CAST")
    return exec { session ->
      session.get(clazz, pk)
    } as T
  }

  fun <T : IEntity> finaAll(clazz: Class<T>): List<T> {
    @Suppress("UNCHECKED_CAST")
    return exec { it.createCriteria(clazz).list().distinct() } as List<T>
  }

  fun saveOrUpdate(entity: IEntity) {
    exec { session -> session.saveOrUpdate(entity) }
  }
}

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
  }

  private fun wrap(wrapper: E): E {
    map[wrapper.primaryKey()] = wrapper
    return wrapper
  }

  operator fun get(primaryKey: Serializable): E? = map[primaryKey]
}

/**
 * wrap the database entity for convenience of use
 * contain entity or not, unknown now, waiting for more discover
 */
abstract class EntityWrapper<T : IEntity> {

  // primaryKey
  abstract fun primaryKey(): Serializable

  // time duration of data change check
  abstract fun duration(): Duration

  // toEntity for database save
  abstract fun toEntity(): T
}

class PlayerAccount(
    entity: PlayerAccountEntity
) : EntityWrapper<PlayerAccountEntity>() {

  override fun primaryKey(): Serializable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun duration(): Duration {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun toEntity(): PlayerAccountEntity {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
