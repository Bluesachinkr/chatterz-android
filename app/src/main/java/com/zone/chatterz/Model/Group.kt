package com.zone.chatterz.Model

import com.google.firebase.database.PropertyName

data class Group(

    @set:PropertyName("groupName")
    @get:PropertyName("groupName")
    var groupName : String,
    @set:PropertyName("groupImgUrl")
    @get:PropertyName("groupImgUrl")
    var groupImgUrl : String,
    var groupMaker : String,
    var id : String

){
    constructor():this("","","","")
}