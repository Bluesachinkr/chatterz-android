package com.zone.chatterz.FirebaseConnection

import com.google.firebase.database.DataSnapshot

open class RequestCallback{
    open fun onDataChanged(dataSnapshot: DataSnapshot){
        println("DOne")
    }
}