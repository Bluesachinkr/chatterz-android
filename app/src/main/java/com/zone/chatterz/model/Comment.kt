package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class Comment(

    @set:PropertyName("message")
    @get:PropertyName("message")
    var message : String,
    @set:PropertyName("sender")
    @get:PropertyName("sender")
    var sender : String,
    @set:PropertyName("heart")
    @get:PropertyName("heart")
    var heart : String,
    @set:PropertyName("likes")
    @get:PropertyName("likes")
    var likes : Long
){
    constructor():this("","","",0)
}