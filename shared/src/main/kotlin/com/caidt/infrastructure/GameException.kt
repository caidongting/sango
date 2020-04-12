package com.caidt.infrastructure

import com.caidt.share.Reason


class ServerException(val reason: Reason, msg: String) : Throwable(msg)

class GameException(val reason: Reason, msg: String) : Throwable(msg)
