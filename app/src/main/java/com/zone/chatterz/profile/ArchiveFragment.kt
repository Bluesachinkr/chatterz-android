package com.zone.chatterz.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.adapter.ImageAdapter
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.model.Post
import java.io.File
import java.util.*

class ArchiveFragment(mContext : Context,user : String) : Fragment() {

    private val mContext = mContext
    private val user = user
    private lateinit var grid_view_archive_profile : RecyclerView
    private val mPostList = arrayListOf<Post>()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_archive, container, false)

        //initiliazation
        grid_view_archive_profile = view.findViewById(R.id.grid_view_archive_profile)
        val layoutManager = GridLayoutManager(mContext,3)
        grid_view_archive_profile.layoutManager = layoutManager
        imageAdapter = ImageAdapter(mContext,mPostList)
        grid_view_archive_profile.adapter = imageAdapter
        setGridViewAdapter()

        return view
    }

    private fun setGridViewAdapter() {
        val ref = Connection.archivePost+ File.separator+user
        FirebaseMethods.addValueEvent(ref,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                mPostList.clear()
                for (data in dataSnapshot.children){
                    val post = data.getValue(Post::class.java)
                    post?.let {
                        if(post.postOwner == user){
                            mPostList.add(post)
                        }
                    }
                }
                Collections.reverse(mPostList)
                imageAdapter.notifyDataSetChanged()
            }
        })
    }
}