package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class Comment(
    @set:PropertyName("commentId")
    @get:PropertyName("commentId")
    var commentId: String,
    @set:PropertyName("postId")
    @get:PropertyName("postId")
    var postId: String,
    @set:PropertyName("message")
    @get:PropertyName("message")
    var message: String,
    @set:PropertyName("sender")
    @get:PropertyName("sender")
    var sender: String,
    @set:PropertyName("heart")
    @get:PropertyName("heart")
    var heart: Boolean,
    @set:PropertyName("likes")
    @get:PropertyName("likes")
    var likes: Long,
    @set:PropertyName("time")
    @get:PropertyName("time")
    var time: String,
    @set:PropertyName("replyCount")
    @get:PropertyName("replyCount")
    var replyCount: Long,
    @set:PropertyName("isComment")
    @get:PropertyName("isComment")
    var isComment : Boolean,
    @set:PropertyName("toReply")
    @get:PropertyName("toReply")
    var toReply : String,
    @set:PropertyName("parent")
    @get:PropertyName("parent")
    var parent : String
) {
    constructor() : this("","","", "", false, 0, "", 0,true,"","")
}