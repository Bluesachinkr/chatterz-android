package com.zone.chatterz.Model

import com.google.firebase.database.PropertyName

data class Chat(
    @set:PropertyName("message")
    @get:PropertyName("message")
    var message: String,
    @set:PropertyName("sender")
    @get:PropertyName("sender")
    var sender: String,
    @set:PropertyName("receiver")
    @get:PropertyName("receiver")
    var receiver: String,
    @set:PropertyName("isSeen")
    @get:PropertyName("isSeen")
    var isSeen: Boolean
) {
    constructor() : this(
        "", "",
        "", false
    )
}
