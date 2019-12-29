package com.caidt

/** client property */
annotation class Client

/** server property */
annotation class Server

data class Item(
    /** item uid */
    val uid: Long,
    /** item owner */
    val owner: Long,
    val name: String,
    val catalog: Int,
    val type: Int,
    var num: Long
)
