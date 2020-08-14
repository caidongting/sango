package com.caidt.infrastructure.database

import com.caidt.share.entity.PlayerEntity
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.criterion.Restrictions
import java.io.Serializable
import javax.persistence.Column


class Session(private val sessionFactory: SessionFactory) {

  fun <T> exec(task: (session: Session) -> T?): T? {
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
    return exec { session ->
      session.createCriteria(clazz).list().distinct()
//      val createQuery = session.criteriaBuilder.createQuery(clazz).also { it.from(clazz) }
//      session.createQuery(createQuery).resultList.distinct()
    } as List<T>
  }

  fun <T : PlayerEntity> findByPlayerId(clazz: Class<T>, playerId: Long): List<T> {
    val declaredField = clazz.getDeclaredField(PlayerEntity::playerId.name)
    val name = declaredField.getAnnotation(Column::class.java).name
    @Suppress("UNCHECKED_CAST")
    return exec { session ->
      session.createCriteria(clazz).add(Restrictions.eq(name, playerId)).list().distinct()
//      val builder = session.criteriaBuilder
//      val criteriaQuery = builder.createQuery(clazz).apply {
//        val root = from(clazz)
//        where(builder.equal(root.get<String>(name), playerId))
//      }
//      session.createQuery(criteriaQuery).resultList.distinct()
    } as List<T>
  }

  fun saveOrUpdate(entity: IEntity) {
    exec { session -> session.saveOrUpdate(entity) }
  }

  fun delete(entity: IEntity) {
    exec { session -> session.delete(entity) }
  }
}
