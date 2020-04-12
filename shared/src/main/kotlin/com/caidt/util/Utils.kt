package com.caidt.util


/**
 * 此处为最外层，因此不再抛出
 */
fun tryCatch(block: () -> Unit) {
  try {
    block()
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
