package com.caidt.util

/**
 * 树结构
 */
class Tree<E> {

  private lateinit var root: Node<E>
  private var comparator: Comparator<E>? = null

  sealed class Node<E>(
    val data: E,

    var parent: Node<E>?,
    var left: Node<E>?,
    var right: Node<E>?
  ) {
    val isRoot: Boolean get() = parent == null
    val isLeaf: Boolean get() = left == null && right == null
  }

  companion object {
  }

}

