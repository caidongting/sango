package com.caidt.infrastructure

import com.caidt.util.scanPackage
import com.esotericsoftware.kryo.Kryo
import com.typesafe.config.ConfigFactory
import io.altoo.akka.serialization.kryo.KryoSerializationSettings


fun registerKryo() {

  val config = ConfigFactory.load("kryo-serialization.conf")
  KryoSerializationSettings(config)
  val kryo = Kryo()
  kryo.references = false

  // 动态注册excel目录class，配置文件
  scanPackage(EXCEL_CONFIG_DIR).forEach {
    kryo.register(it)
  }

  kryo.fieldSerializerConfig
}