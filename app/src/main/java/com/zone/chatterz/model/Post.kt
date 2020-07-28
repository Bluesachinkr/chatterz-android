package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class Post(
    @get:PropertyName("postOwner")
    @set:PropertyName("postOwner")
    var postOwner : String,
    @get:PropertyName("postImage")
    @set:PropertyName("postImage")
    var postImage : String,
    @get:PropertyName("postTitle")
    @set:PropertyName("postTitle")
    var postTitle : String,
    @get:PropertyName("postHashTags")
    @set:PropertyName("postHashTags")
    var postHashTags : String,
    @get:PropertyName("postTime")
    @set:PropertyName("postTime")
    var postTime : String

) {
    constructor():this("","","","","")
}