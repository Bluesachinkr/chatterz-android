package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User

class HomeAdapter(mContext : Context,postList: MutableList<Post>/*,usersList : MutableList<User>*/) : RecyclerView.Adapter<HomeAdapter.Viewholder>() {

    private val mContext = mContext
    private val postList = postList
  /*  private val usersList = usersList*/

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profileImage_post : CircularImageView = itemView.findViewById(R.id.profileImage_post)
        val profileName_post : TextView = itemView.findViewById(R.id.profileName_post)
        val options_post : ImageView = itemView.findViewById(R.id.options_post)
        val image_post : ImageView = itemView.findViewById(R.id.image_post)
        val like_post : ImageView = itemView.findViewById(R.id.like_post)
        val no_of_likes_post : TextView = itemView.findViewById(R.id.no_of_likes_post)
        val comment_post : ImageView = itemView.findViewById(R.id.comment_post)
        val no_of_comments_post : TextView = itemView.findViewById(R.id.no_of_comments_post)
        val description_post : TextView = itemView.findViewById(R.id.description_post)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return this.postList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post = postList.get(position)
       /* val user = usersList.get(position)*/

       /* //setting profile image
        Glide.with(mContext).load(user.imageUrl).into(holder.profileImage_post)

        //setting profile name
        holder.profileName_post.text = user.username*/

        //setting image on post
        Glide.with(mContext).load(post.postImage).into(holder.image_post)

        //setting description on post
        holder.description_post.text = post.postDescription
    }
}