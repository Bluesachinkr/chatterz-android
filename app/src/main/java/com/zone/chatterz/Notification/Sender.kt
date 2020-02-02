package com.zone.chatterz.Notification

data class Sender(

    var data: Data?,
    var to: String
) {
    constructor() : this(null, "")
}