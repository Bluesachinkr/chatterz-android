package com.zone.chatterz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.FriendsAdapter
import com.zone.chatterz.Model.User


open class FriendOnlineActivity : Fragment() {

    private lateinit var followerRecyclerView: RecyclerView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var mUsers: MutableList<User>
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_followers, container, false)

        followerRecyclerView = view.findViewById(R.id.followerRecyclerView)

        followerRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.context)
        followerRecyclerView.layoutManager = linearLayoutManager

        mUsers = mutableListOf()
        readFriendsOnline()

        return view
    }

    private fun readFriendsOnline() {
        val list = mutableListOf<String>()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val friends = data.key!!
                    list.add(friends)
                }
                val dataRef = FirebaseDatabase.getInstance().getReference("Users")
                dataRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            val user = data.getValue(User::class.java)
                            if (user != null && list.contains(user.id) && user.status.equals("online")) {
                                mUsers.add(user)
                            }
                        }
                        val getContext = context!!
                        friendsAdapter = FriendsAdapter(getContext, mUsers)
                        followerRecyclerView.adapter = friendsAdapter
                    }

                })
            }
        })
    }

}
