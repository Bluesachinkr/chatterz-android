package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class PopUp(
    @get:PropertyName("message")
    @set:PropertyName("message")
    var message : String,
    @get:PropertyName("sender")
    @set:PropertyName("sender")
    var sender : String,
    @get:PropertyName("image")
    @set:PropertyName("image")
    var image : String

) {
    constructor():this("","","")
}