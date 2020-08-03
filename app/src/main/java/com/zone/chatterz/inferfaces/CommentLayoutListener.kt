package com.zone.chatterz.inferfaces

interface CommentLayoutListener {
    fun onReplyInfo(parent: String, to: String)
    fun onCommentReplyEdit(message: String)
}