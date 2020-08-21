package com.zone.chatterz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.adapter.ImageAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PhotosPostProfileFragment(mContext : Context,user: String) : Fragment() {

    private val mContext = mContext
    private val user = user
    private lateinit var grid_view_photos_post_profile : RecyclerView
    private val mPostList = arrayListOf<Post>()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_photos_post_profile, container, false)

        //initiliazation
        grid_view_photos_post_profile = view.findViewById(R.id.grid_view_photos_post_profile)
        val layoutManager = GridLayoutManager(mContext,3)
        grid_view_photos_post_profile.layoutManager = layoutManager
        imageAdapter = ImageAdapter(mContext,mPostList)
        grid_view_photos_post_profile.adapter = imageAdapter
        setGridViewAdapter()
        return view
    }

    private fun setGridViewAdapter() {
        val ref = Connection.postRef
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