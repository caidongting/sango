package com.caidt.util

import com.caidt.infrastructure.GameException
import com.caidt.share.Reason


fun assertTrue(condition: Boolean, reason: Reason, msg: String) {
  if (!condition)
    throw GameException(reason, msg)
}

fun assertFalse(condition: Boolean, reason: Reason, msg: String) {
  if (condition)
    throw GameException(reason, msg)
}

fun assertNull(obj: Any?, reason: Reason, msg: String) {
  if (obj != null)
    throw GameException(reason, msg)
}

fun assertNotNull(obj: Any?, reason: Reason, msg: String) {
  if (obj == null)
    throw GameException(reason, msg)
}

inline fun <T> notNull(obj: T?, lazyMessage: () -> String = { "required not null" }): T {
  if (obj == null)
    throw GameException(Reason.NPE, lazyMessage())
  return obj
}
