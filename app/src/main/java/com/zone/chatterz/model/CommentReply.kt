package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class CommentReply(
    @set:PropertyName("toReply")
    @get:PropertyName("toReply")
    var toReply : String,
    @set:PropertyName("onReplyParent")
    @get:PropertyName("onReplyParent")
    var onReplyParent : String
) {
    constructor():this("","")
}