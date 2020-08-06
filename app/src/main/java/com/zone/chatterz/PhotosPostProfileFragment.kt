package com.zone.chatterz

import android.content.Context
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
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import java.io.File
import java.util.*

class PhotosPostProfileFragment(mContext : Context,user: String) : Fragment() {

    private val mContext = mContext
    private val user = user
    private lateinit var grid_view_photos_post_profile : RecyclerView
    private val mPostList = mutableListOf<Post>()
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

    private class ImageAdapter(mContext: Context,mPostList : MutableList<Post>) : RecyclerView.Adapter<ImageAdapter.ImageViewholder>(){

        private val mContext = mContext
        private val mPostList = mPostList

        private class ImageViewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            val imageView : ImageView = itemView.findViewById(R.id.post_image_item_profile)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewholder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.post_profile_item,parent,false)
            return ImageViewholder(view)
        }

        override fun getItemCount(): Int {
            return mPostList.size
        }

        override fun onBindViewHolder(holder: ImageViewholder, position: Int) {
            val post = mPostList[position]

            Glide.with(mContext).load(post.postImage).into(holder.imageView)
        }
    }
}