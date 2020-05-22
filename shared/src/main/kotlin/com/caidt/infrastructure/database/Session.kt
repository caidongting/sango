package com.caidt.infrastructure.database

import org.hibernate.Session
import org.hibernate.SessionFactory
import java.io.Serializable


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

  fun saveOrUpdate(entity: IEntity) {
    exec { session -> session.saveOrUpdate(entity) }
  }
}
