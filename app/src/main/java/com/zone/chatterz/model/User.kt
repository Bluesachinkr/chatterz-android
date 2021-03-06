package com.zone.chatterz.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

data class User (

    @Exclude val id: String,
    @set:PropertyName("username")
    @get:PropertyName("username")
    var username: String,
    @set:PropertyName("displayName")
    @get:PropertyName("displayName")
    var displayName: String,
    @set:PropertyName("imageUrl")
    @get:PropertyName("imageUrl")
    var imageUrl: String,
    @set:PropertyName("bio")
    @get:PropertyName("bio")
    var bio: String,
    var gender : String,
    var status : String

){
    constructor():this("","","","","","","")
}