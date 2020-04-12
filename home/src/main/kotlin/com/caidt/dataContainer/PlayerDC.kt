package com.caidt.dataContainer

import com.caidt.infrastructure.database.DataContainer
import com.caidt.infrastructure.database.EntityWrapper
import com.caidt.share.entity.PlayerAccountEntity
import java.io.Serializable
import java.time.Duration

class PlayerDC : DataContainer<PlayerAccountEntity, PlayerAccount>() {


  override fun load(entity: PlayerAccountEntity) {
    val e = wrap(entity, PlayerAccount::class.java)
  }

}

class PlayerAccount : EntityWrapper<PlayerAccountEntity>() {

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