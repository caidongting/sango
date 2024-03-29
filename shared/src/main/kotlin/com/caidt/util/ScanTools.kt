package com.caidt.util

import java.io.File
import java.net.JarURLConnection
import java.util.jar.JarFile

/**
 * 扫描[pkgName]下面的类, 可过滤
 */
fun scanPackage(pkgName: String, filter: (Class<*>) -> Boolean = { true }): List<Class<*>> {
  val result: MutableList<Class<*>> = arrayListOf()
  val urls = Thread.currentThread().contextClassLoader.getResources(pkgName.replace('.', '/'))
  for (url in urls) {
    when (url.protocol) {
      "file" -> scanFile(pkgName, File(url.file), result, filter)
      "jar" -> {
        val urlConnection = url.openConnection() as JarURLConnection
        scanJar(urlConnection.jarFile, result, filter)
      }
      else -> Unit
    }
  }

  return result
}


internal fun scanFile(pkgName: String, file: File, result: MutableList<Class<*>>, filter: (Class<*>) -> Boolean) {
  if (file.isDirectory) {
    file.listFiles()?.forEach {
      val newPkgName = if (it.isDirectory) pkgName + "." + it.name else pkgName
      scanFile(newPkgName, it, result, filter)
    }
  } else if (file.name.endsWith(".class")) {
    val classname = file.name.replace(".class", "")
    val clazz = Class.forName("$pkgName.$classname")
    if (check(clazz) && filter(clazz)) {
      result.add(clazz)
    }
  }
}

internal fun scanJar(jarFile: JarFile, result: MutableList<Class<*>>, filter: (Class<*>) -> Boolean) {
  for (jarEntry in jarFile.entries()) {
    if (jarEntry.name.endsWith(".class")) {
      val classname = jarEntry.name.substring(0, jarEntry.name.lastIndexOf('.'))
      val clazz = Class.forName(classname)
      if (check(clazz) && filter(clazz)) {
        result.add(clazz)
      }
    }
  }
}

internal fun check(clazz: Class<*>): Boolean {
  return when {
    clazz.isSynthetic -> false // 合成类
    clazz.isAnnotation -> false // 注解类
//    clazz.isInterface -> false // 接口
    clazz.isAnonymousClass -> false // 匿名类
    clazz.isLocalClass -> false // 本地类
    else -> true
  }
}