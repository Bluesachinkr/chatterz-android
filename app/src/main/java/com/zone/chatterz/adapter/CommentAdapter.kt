package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.MainActivity
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.inferfaces.CommentReloadListener
import com.zone.chatterz.mainFragment.HomeActivity
import com.zone.chatterz.model.Comment
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import java.io.File

class CommentAdapter(mContext: Context, commentList: MutableList<Comment>,mCommentReloadListener : CommentReloadListener) :
    RecyclerView.Adapter<CommentAdapter.Viewholder>() {

    private val mContext = mContext
    private val commentList = commentList

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val comment_pic = itemView.findViewById<CircularImageView>(R.id.comment_pic)
        val comment_username = itemView.findViewById<TextView>(R.id.comment_username)
        val comment_time = itemView.findViewById<TextView>(R.id.comment_time)
        val comment_message = itemView.findViewById<TextView>(R.id.comment_message)
        val comment_likes = itemView.findViewById<TextView>(R.id.comment_likes)
        val comment_reply = itemView.findViewById<TextView>(R.id.comment_reply)
        val comment_heart = itemView.findViewById<ImageView>(R.id.comment_heart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_view, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val comment = commentList.get(position)

        holder.comment_message.text = comment.message

        holder.comment_time.text = Timings.timeUploadPost(comment.time)

        if (comment.heart) {
            holder.comment_heart.setImageResource(R.drawable.ic_heart_filled)
        }

        if (comment.likes == 0.toLong()) {
            holder.comment_likes.text = "likes"
        }else{
            val builder = StringBuilder(comment.likes.toString())
            builder.append(" likes")
            holder.comment_likes.text = builder.toString()
        }

        loadUserInfo(holder, comment.sender)

        holder.comment_reply.setOnClickListener {
            val builder = StringBuilder("@")
            if(comment.onReplyParent.equals("null")){
                builder.append(comment.sender)
            }else{
                builder.append(comment.onReplyParent)
            }
            builder.append(" ")
            (mContext as HomeActivity.NavigationControls).onCommentReplyEdit(builder.toString())
        }
    }

    private fun loadUserInfo(holder: Viewholder, sender: String) {
        val reference = Connection.userRef+ File.separator+sender
        FirebaseMethods.singleValueEvent(reference,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    holder.comment_username.text = user.username
                    if(user.imageUrl.equals("null")){
                        holder.comment_pic.setImageResource(R.drawable.google_logo)
                    }else{
                        Glide.with(mContext).load(user.imageUrl).into(holder.comment_pic)
                    }
                }
            }
        })
    }
}