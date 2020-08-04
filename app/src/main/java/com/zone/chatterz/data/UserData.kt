package com.zone.chatterz.data

import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.User
import java.io.File

class UserData {
    companion object {
        var username: String = ""
        var userId: String = ""
        var imageUrl: String = ""
        var online: String = ""
        var bio: String = ""

        fun onUserInfo() {
            FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + Connection.user,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            username = user.username
                            userId = user.id
                            imageUrl = user.imageUrl
                            online = user.status
                            bio = user.bio
                        }
                    }
                })
        }
    }
}