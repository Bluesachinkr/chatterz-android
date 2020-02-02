package com.zone.chatterz.Notification

data class Data(

    var user: String,
    var icon: String,
    var body: String,
    var title: String,
    var sented: String
) {
    constructor() : this("", "", "", "", "")

}