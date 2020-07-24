package com.zone.chatterz.firebaseConnection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FirebaseMethods {
    companion object {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser = mAuth.currentUser!!
        lateinit var databaseReference: DatabaseReference

        fun addValueEvent(reference: String, requestCallback: RequestCallback) {
            databaseReference = FirebaseDatabase.getInstance().getReference(reference)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    requestCallback.onDataChanged(p0)
                }
            })
        }

        fun singleValueEvent(reference: String, requestCallback: RequestCallback) {
            databaseReference = FirebaseDatabase.getInstance().getReference(reference)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    requestCallback.onDataChanged(p0)
                }
            })
        }

        fun singleValueEventChild(reference: String, requestCallback: RequestCallback) {
            databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(
                firebaseUser.uid
            )
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    requestCallback.onDataChanged(p0)
                }
            })
        }

        fun addValueEventChild(reference: String, requestCallback: RequestCallback) {
            databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(
                firebaseUser.uid
            )
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    requestCallback.onDataChanged(p0)
                }
            })
        }
    }
}