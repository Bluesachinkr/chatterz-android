package com.zone.chatterz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zone.chatterz.Adapter.FollowersAdapter
import com.zone.chatterz.Model.User

class FollowersActivity : AppCompatActivity() {

    private lateinit var friendsView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFrindList: MutableList<User>
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)

        mAuth = FirebaseAuth.getInstance()

        friendsView = findViewById(R.id.frndsView)
        toolbar = findViewById(R.id.toolbarFriend)

        val layoutManager = GridLayoutManager(this, 2)
        friendsView.layoutManager = layoutManager
        mFrindList = mutableListOf()
        setFriendView(this)

    }

    private fun setFriendView(c: Context) {
        val list = mutableListOf<String>()
        val firebaseUser = mAuth.currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val friendId = data.key!!
                    list.add(friendId)
                }
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            val user = data.getValue(User::class.java)
                            if (user != null && list.contains(user.id)) {
                                mFrindList.add(user)
                            }
                        }
                        val adapter = FollowersAdapter(c, mFrindList,"friends")
                        friendsView.adapter = adapter
                    }

                })
            }

        })
    }
}
