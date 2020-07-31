package com.caidt.infrastructure.database

import com.caidt.share.entity.PlayerEntity
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import java.io.Serializable
import javax.persistence.Column


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

  fun <T : IEntity> read(clazz: Class<T>, pk: Serializable): T? {
    @Suppress("UNCHECKED_CAST")
    return exec { session -> session.get(clazz, pk) } as T?
  }

  fun <T : IEntity> findAll(clazz: Class<T>): List<T> {
    @Suppress("UNCHECKED_CAST")
    return exec { it.createCriteria(clazz).list().distinct() } as List<T>
  }

  fun <T : PlayerEntity> findByPlayerId(clazz: Class<T>, playerId: Long): List<T> {
    @Suppress("UNCHECKED_CAST")
    return exec { session ->
      val declaredField = clazz.getDeclaredField(PlayerEntity::playerId.name)
      val name = declaredField.getAnnotation(Column::class.java).name
      session.createCriteria(clazz).add(Restrictions.eq(name, playerId)).list().distinct()
    } as List<T>
  }

  fun saveOrUpdate(entity: IEntity) {
    exec { session -> session.saveOrUpdate(entity) }
  }

  fun delete(entity: IEntity) {
    exec { session -> session.delete(entity) }
  }
}
