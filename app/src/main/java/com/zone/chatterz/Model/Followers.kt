package com.zone.chatterz.Model

import com.google.firebase.database.PropertyName

data class Followers(
    @set:PropertyName("followers")
    @get:PropertyName("followers")
    var followers: Array<String>,
    @set:PropertyName("following")
    @get:PropertyName("following")
    var following : Array<String>,
    @set:PropertyName("userId")
    @get:PropertyName("userId")
    var userId : String
){
    constructor():this(arrayOf(), arrayOf(),"")
}
