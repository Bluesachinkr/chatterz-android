package com.zone.chatterz.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        FirebaseAuth.getInstance()?.currentUser?.let { firebaseUser->
            val token: String? = FirebaseInstanceId.getInstance().token
            token?.let {
                updateToken(it, firebaseUser)
            }
        }
    }

    private fun updateToken(token: String, firebaseUser: FirebaseUser) {
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val refreshToken = Token(token)
        reference.child(firebaseUser.uid).setValue(refreshToken)
    }

}