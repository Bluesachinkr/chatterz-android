package com.zone.chatterz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.FollowerAdapter
import com.zone.chatterz.Adapter.RecentAdapter
import com.zone.chatterz.Model.User


open class FollowerActivity : Fragment() {

    private lateinit var followerRecyclerView: RecyclerView

    private lateinit var databaseReference : DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var mUsers: MutableList<User>
    private lateinit var followerAdapter: FollowerAdapter

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
        readFollowers()
        return view
    }

    private fun readFollowers() {
        firebaseUser  = FirebaseAuth.getInstance().currentUser!!

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mUsers.clear()
                for(dataSet in p0.children){
                    val user = dataSet.getValue(User::class.java)
                    if(user != null){
                        if(!user.id.equals(firebaseUser.uid)){
                            mUsers.add(user)
                        }
                    }
                }
                val getContext = context!!
                followerAdapter = FollowerAdapter(getContext,mUsers)
                followerRecyclerView.adapter = followerAdapter
            }
        })

    }

    override fun onResume() {
        super.onResume()
    }
}
