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

class FollowingFragment(mContext: Context) : Fragment() {

    private val mContext = mContext

    private lateinit var followingRecyclerView: RecyclerView
    private lateinit var followingAdapter: FollowersFollowingAdapter

    private val mUserList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_following, container, false)

        followingRecyclerView = view.findViewById(R.id.followingRecyclerView)
        val layoutManager = LinearLayoutManager(mContext)
        followingRecyclerView.layoutManager = layoutManager
        followingAdapter = FollowersFollowingAdapter(mContext, mUserList, null)
        followingRecyclerView.adapter = followingAdapter

        loadData()
        return view
    }

    private fun loadData() {
        mUserList.clear()
        FirebaseMethods.addValueEvent(Connection.followingRef + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        val str = data.key.toString()
                        mUserList.add(str)
                    }
                    followingAdapter.notifyDataSetChanged()
                }
            })
    }

}