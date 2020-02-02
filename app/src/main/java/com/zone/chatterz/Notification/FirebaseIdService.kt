package com.zone.chatterz.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val token: String? = FirebaseInstanceId.getInstance().getToken()

        if (firebaseUser != null && token != null) {
            updateToken(token)
        }
    }

    private fun updateToken(token: String) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val refreshToken = Token(token)
        reference.child(firebaseUser.uid).setValue(refreshToken)

    }

}