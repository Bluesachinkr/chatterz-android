package com.zone.chatterz.FirebaseConnection

import com.google.firebase.auth.FirebaseAuth

open class Connection {
    companion object {
        //user id
        val  user : String = FirebaseAuth.getInstance().currentUser!!.uid
        //database reference
        val userRef: String = "Users"
        val groupsRef: String = "Groups"
        val groupMemRef: String = "GroupMembers"
        val groupJoinedRef: String = "GroupJoined"
        val groupChats: String = "GroupChats"
        val userChats : String = "Chats"
    }
}