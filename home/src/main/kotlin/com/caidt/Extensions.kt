package com.caidt

import com.caidt.share.Ok


/** 通用应答（服务器内部消息） */
fun PlayerActor.answerOk() {
  this.answer(Ok)
}
