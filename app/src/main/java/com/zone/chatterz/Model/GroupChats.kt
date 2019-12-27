package com.zone.chatterz.Model

import com.google.firebase.database.PropertyName

data class GroupChats(

    @set:PropertyName("sender")
    @get:PropertyName("sender")
    var sender: String,
    @set:PropertyName("message")
    @get:PropertyName("message")
    var message: String,
    var dateTime: String
) {
    constructor() : this("", "", "")
}