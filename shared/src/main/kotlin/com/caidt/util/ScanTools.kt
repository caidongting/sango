package com.caidt.util

import java.io.File
import java.net.JarURLConnection
import java.util.jar.JarFile

/**
 * 扫描[packageName]下面的类, 可过滤
 */
fun scanPackage(packageName: String, filter: (Class<*>) -> Boolean = { true }): List<Class<*>> {
  val result: MutableList<Class<*>> = arrayListOf()
  val urls = Thread.currentThread().contextClassLoader.getResources(packageName.replace('.', '/'))
  while (urls.hasMoreElements()) {
    val url = urls.nextElement()
    when (url.protocol) {
      "file" -> scanFile(packageName, File(url.file), result, filter)
      "jar" -> {
        val urlConnection = url.openConnection() as JarURLConnection
        scanJar(urlConnection.jarFile, result, filter)
      }
      else -> Unit
    }
  }

  return result
}


internal fun scanFile(packageName: String, file: File, result: MutableList<Class<*>>, filter: (Class<*>) -> Boolean) {
  if (file.isDirectory) {
    file.listFiles()?.forEach {
      val newPackageName = if (it.isDirectory) packageName + "." + it.name else packageName
      scanFile(newPackageName, it, result, filter)
    }
  } else if (file.name.endsWith(".class")) {
    val classname = file.name.replace(".class", "")
    val clazz = Class.forName("$packageName.$classname")
    if (!clazz.isSynthetic && filter.invoke(clazz)) {
      result.add(clazz)
    }
  }
}

internal fun scanJar(jarFile: JarFile, result: MutableList<Class<*>>, filter: (Class<*>) -> Boolean) {
  val entries = jarFile.entries()
  while (entries.hasMoreElements()) {
    val jarEntry = entries.nextElement()
    if (jarEntry.name.endsWith(".class")) {
      val classname = jarEntry.name.substring(0, jarEntry.name.lastIndexOf('.'))
      val clazz = Class.forName(classname)
      if (!clazz.isSynthetic && filter.invoke(clazz)) {
        result.add(clazz)
      }
    }
  }
}