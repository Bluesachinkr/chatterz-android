package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.PostsActivity
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Comment
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import java.io.File

class ExploreAdapter(mContext: Context, postList: ArrayList<Post>) :
    RecyclerView.Adapter<ExploreAdapter.Viewholder>() {

    private val mContext = mContext
    private val postList = postList

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage_post_explore: CircularImageView =
            itemView.findViewById(R.id.profileImage_post_explore)
        val profileName_post_explore: TextView =
            itemView.findViewById(R.id.profileName_post_explore)
        val image_post_explore: ImageView = itemView.findViewById(R.id.image_post_explore)
        val like_post_explore: ImageView = itemView.findViewById(R.id.like_post_explore)
        val time_post_explore: TextView = itemView.findViewById(R.id.time_post_explore)
        val no_of_likes_post_explore: TextView =
            itemView.findViewById(R.id.no_of_likes_post_explore)
        val no_of_comments_post_explore: TextView =
            itemView.findViewById(R.id.no_of_comments_post_explore)
        val description_post_explore: TextView =
            itemView.findViewById(R.id.description_post_explore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.layout_explore_post, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post = postList[position]
        setUpProfileData(post.postOwner, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, PostsActivity::class.java)
            intent.putExtra("list",postList)
            mContext.startActivity(intent)
        }
        //image
        Glide.with(mContext).load(post.postImage).into(holder.image_post_explore)

        //setting description on post
        holder.description_post_explore.text = post.postDescription

        //settting time post uploaded
        holder.time_post_explore.text = Timings.timeUploadPost(post.postTime)

        likesCount(post.postId, holder)

        commentsCount(post.postId, holder)

        FirebaseMethods.singleValueEvent(Connection.likesRef + File.separator + post.postId + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        holder.like_post_explore.setImageResource(R.drawable.ic_filled_like_star_post)
                    }
                }
            })
    }

    private fun commentsCount(postId: String, holder: Viewholder) {
        var commentsCount: Long = 0
        FirebaseMethods.addValueEventDifferentChild(
            Connection.commentsRef,
            postId,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    commentsCount += dataSnapshot.childrenCount
                    for (data in dataSnapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        comment?.let {
                            commentsCount += comment.replyCount
                        }
                    }
                    holder.no_of_comments_post_explore.text = commentsCount.toString()
                }
            })
    }

    private fun likesCount(
        postId: String,
        holder: Viewholder
    ) {
        FirebaseMethods.singleValueEventDifferentChild(
            Connection.likesRef,
            postId,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.childrenCount
                    val zero: Long = 0
                    if (count == zero) {
                        holder.no_of_likes_post_explore.text = "0"
                    } else {
                        holder.no_of_likes_post_explore.text = count.toString()
                    }
                }
            })
    }

    private fun setUpProfileData(postOwner: String, holder: ExploreAdapter.Viewholder) {
        FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + postOwner,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {

                        //Profile Image
                        if (user.imageUrl.equals("null")) {
                            if (user.gender.equals("Male")) {
                                holder.profileImage_post_explore.setImageResource(R.drawable.ic_male_gender_profile)
                            } else {
                                holder.profileImage_post_explore.setImageResource(R.drawable.ic_female_gender_profile)
                            }
                        } else {
                            Glide.with(mContext).load(user.imageUrl)
                                .into(holder.profileImage_post_explore)
                        }

                        //Proilfe Name
                        holder.profileName_post_explore.text = user.username
                    }
                }
            })
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}