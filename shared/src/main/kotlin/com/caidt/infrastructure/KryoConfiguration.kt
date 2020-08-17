package com.caidt.infrastructure

import com.caidt.util.scanPackage
import com.esotericsoftware.kryo.serializers.VersionFieldSerializer
import io.altoo.akka.serialization.kryo.DefaultKryoInitializer
import io.altoo.akka.serialization.kryo.serializer.scala.ScalaKryo

class KryoConfiguration : DefaultKryoInitializer() {

  // todo: versionFieldSerializer @Since ...
  override fun preInit(kryo: ScalaKryo) {
    kryo.setDefaultSerializer(VersionFieldSerializer::class.java)
  }

  override fun postInit(kryo: ScalaKryo) {
//    kryo.references = false
//    kryo.isRegistrationRequired = true // need register or not
//    kryo.instantiatorStrategy = StdInstantiatorStrategy() // for no constructor

    // 注册java常用util类
//    scanPackage("java.util").forEach {
//      kryo.register(it)
//    }
    // 动态注册excel目录class，配置文件
    scanPackage(EXCEL_CONFIG_DIR) {
      !it.name.contains('$') && !it.simpleName.endsWith("Kt") // 过滤匿名类、伴生类，顶级Kt类（包含顶级方法或属性）
    }.forEach {
      kryo.register(it)
    }
  }
}
