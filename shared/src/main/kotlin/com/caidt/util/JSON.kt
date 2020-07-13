@file:Suppress("unused")

package com.caidt.util

import com.fasterxml.jackson.databind.*

object JSON {

  private val json = ObjectMapper()

  private val LIST_INT_TYPE = json.typeFactory.constructCollectionType(List::class.java, Int::class.java)
  private val LIST_LONG_TYPE = json.typeFactory.constructCollectionType(List::class.java, Long::class.java)

  init {
    // SerializationFeature for changing how JSON is written

    // to enable standard indentation ("pretty-printing"):
    json.enable(SerializationFeature.INDENT_OUTPUT);
    // to allow serialization of "empty" POJOs (no properties to serialize)
    // (without this setting, an exception is thrown in those cases)
    json.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    // to write java.util.Date, Calendar as number (timestamp):
    json.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // DeserializationFeature for changing how JSON is read as POJOs:

    // json.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
    // to prevent exception when encountering unknown property:
    json.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    // to allow coercion of JSON empty String ("") to null Object value:
    json.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
  }

  fun toJSONString(obj: Any): String {
    return json.writeValueAsString(obj)
  }

  fun toByteArray(obj: Any): ByteArray {
    return json.writeValueAsBytes(obj)
  }

  fun <T> parseObject(source: String, clazz: Class<T>): T {
    return json.readValue(source, clazz)
  }

  fun <T> parseObject(bytes: ByteArray, clazz: Class<T>): T {
    return json.readValue(bytes, clazz)
  }

  fun <T> parseArray(source: String, clazz: Class<T>): MutableList<T> {
    val javaType = constructListType(clazz)
    return json.readValue(source, javaType)
  }

  fun <T> parseArray(bytes: ByteArray, clazz: Class<T>): MutableList<T> {
    val javaType = constructListType(clazz)
    return json.readValue(bytes, javaType)
  }

  private fun constructListType(elementType: Class<*>): JavaType {
    return when (elementType) {
      Int::class.java -> LIST_INT_TYPE
      Long::class.java -> LIST_LONG_TYPE
      else -> json.typeFactory.constructCollectionType(List::class.java, elementType)
    }
  }

  private fun constructCollectionType(parameterized: Class<Collection<*>>, elementType: Class<*>): JavaType {
    return when (parameterized) {
      List::class.java -> constructListType(elementType) // 优化
      else -> json.typeFactory.constructCollectionType(parameterized, elementType)
    }
  }

}
