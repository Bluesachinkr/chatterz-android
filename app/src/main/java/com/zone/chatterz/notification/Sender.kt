package com.zone.chatterz.notification

data class Sender(

    var data: Data?,
    var to: String
) {
    constructor() : this(null, "")
}