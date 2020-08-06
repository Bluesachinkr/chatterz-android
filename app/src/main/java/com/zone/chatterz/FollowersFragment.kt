package com.zone.chatterz

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.adapter.FollowersFollowingAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import java.io.File


class FollowersFragment(mContext: Context) : Fragment() {

    private val mContext = mContext

    private lateinit var followersRecyclerView: RecyclerView
    private lateinit var adapter: FollowersFollowingAdapter
    private val mUsersList = mutableListOf<String>()
    private val hashMap = hashMapOf<String, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_followers, container, false)

        followersRecyclerView = view.findViewById(R.id.followersRecyclerView)
        val layoutManager = LinearLayoutManager(mContext)
        followersRecyclerView.layoutManager = layoutManager
        adapter = FollowersFollowingAdapter(mContext, mUsersList, hashMap)
        followersRecyclerView.adapter = adapter

        loadData()
        return view
    }

    private fun loadData() {
        mUsersList.clear()
        FirebaseMethods.addValueEvent(Connection.followersRef + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        val follower = data.key.toString()
                        hashMap.put(follower, false)
                        mUsersList.add(follower)
                        FirebaseMethods.addValueEvent(Connection.followingRef + File.separator + Connection.user,
                            object : RequestCallback() {
                                override fun onDataChanged(dataSnap: DataSnapshot) {
                                    for (value in dataSnap.children) {
                                        val str = value.key.toString()
                                        if (hashMap.containsKey(str)) {
                                            hashMap.put(str, true)
                                        }
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                            })
                    }
                }
            })
    }

}