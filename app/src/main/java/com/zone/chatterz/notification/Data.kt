package com.zone.chatterz.notification

data class Data(

    var user: String,
    var icon: String,
    var body: String,
    var title: String,
    var sented: String
) {
    constructor() : this("", "", "", "", "")

}