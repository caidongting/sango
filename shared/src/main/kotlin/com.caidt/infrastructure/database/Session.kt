package com.caidt.infrastructure.database

import org.hibernate.Session
import org.hibernate.SessionFactory
import java.io.Serializable

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

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T : IEntity> read(clazz: Class<T>, pk: Serializable): T {
    return exec { session ->
      session.get(clazz, pk)
    } as T
  }

  fun <T : IEntity> finaAll(clazz: Class<T>): List<T> {
    return exec { it.createCriteria(clazz).list().distinct() } as List<T>
  }

  fun save(entity: IEntity) {
    exec { session -> session.saveOrUpdate(entity) }
  }
}

