package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zone.chatterz.PostsActivity
import com.zone.chatterz.R
import com.zone.chatterz.model.Post

class ImageAdapter(mContext: Context, mPostList : ArrayList<Post>) : RecyclerView.Adapter<ImageAdapter.ImageViewholder>(){

    private val mContext = mContext
    private val mPostList = mPostList

    class ImageViewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {
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

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, PostsActivity::class.java)
            intent.putExtra("list",mPostList)
            mContext.startActivity(intent)
        }
    }
}