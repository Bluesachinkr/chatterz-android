package com.zone.chatterz.model

import com.google.firebase.database.PropertyName

data class Post(
    @get:PropertyName("postId")
    @set:PropertyName("postId")
    var postId : String,
    @get:PropertyName("postOwner")
    @set:PropertyName("postOwner")
    var postOwner : String,
    @get:PropertyName("postImage")
    @set:PropertyName("postImage")
    var postImage : String,
    @get:PropertyName("postDescription")
    @set:PropertyName("postDescription")
    var postDescription : String,
    @get:PropertyName("postTime")
    @set:PropertyName("postTime")
    var postTime : String

) {
    constructor():this("","","","","")
}