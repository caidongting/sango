package com.caidt.infrastructure


class ServerException(val reason: Reason, msg: String) : Throwable(msg)

class GameException(val reason: Reason, msg: String) : Throwable(msg)
