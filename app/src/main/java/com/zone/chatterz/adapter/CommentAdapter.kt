package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
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

class CommentAdapter(
    mContext: Context,
    commentList: MutableList<Comment>,
    mCommentReloadListener: CommentReloadListener
    , hashMap: HashMap<String, Boolean>
) :
    RecyclerView.Adapter<CommentAdapter.Viewholder>() {

    private val mContext = mContext
    private val commentList = commentList
    private val hashMap: HashMap<String, Boolean> = hashMap
    private val mCommentReloadListener: CommentReloadListener = mCommentReloadListener
    private val COMMENT_TYPE = 1
    private val REPLY_TYPE = 2

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val comment_pic = itemView.findViewById<CircularImageView>(R.id.comment_pic)
        val comment_username = itemView.findViewById<TextView>(R.id.comment_username)
        val comment_time = itemView.findViewById<TextView>(R.id.comment_time)
        val comment_message = itemView.findViewById<TextView>(R.id.comment_message)
        val comment_likes = itemView.findViewById<TextView>(R.id.comment_likes)
        val comment_reply = itemView.findViewById<TextView>(R.id.comment_reply)
        val comment_heart = itemView.findViewById<ImageView>(R.id.comment_heart)
        val comment_view_all_replies = itemView.findViewById<LinearLayout>(R.id.view_all_replies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        if(viewType == COMMENT_TYPE){
            val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_view, parent, false)
            return Viewholder(view)
        }else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.comment_reply_item_view, parent, false)
            return Viewholder(view)
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val comment = commentList.get(position)

        if (comment.isReply > 0) {
            holder.comment_view_all_replies.visibility = View.VISIBLE
        }
        holder.comment_message.text = comment.message

        holder.comment_time.text = Timings.timeUploadPost(comment.time)

        if (comment.heart) {
            holder.comment_heart.setImageResource(R.drawable.ic_heart_filled)
        }

        if (comment.likes == 0.toLong()) {
            holder.comment_likes.text = "likes"
        } else {
            val builder = StringBuilder(comment.likes.toString())
            builder.append(" likes")
            holder.comment_likes.text = builder.toString()
        }

        loadUserInfo(holder, comment.sender)

        holder.comment_view_all_replies.setOnClickListener {
            if (!hashMap[comment.sender]!!) {
                hashMap.put(comment.sender, true)
            } else {
                hashMap.put(comment.sender, false)
            }
            mCommentReloadListener.onReload("", comment.postId, hashMap, comment.sender)
        }

        holder.comment_reply.setOnClickListener {
            val builder = StringBuilder("@")
            var key = ""
            if (comment.isComment) {
                key = comment.sender
            } else {
                key = comment.parent
            }

            FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + key,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        val data = dataSnapshot.getValue(User::class.java)
                        data?.let {
                            builder.append(data.username)
                            builder.append(" ")
                            (mContext as HomeActivity.NavigationControls).onCommentReplyEdit(builder.toString())
                            (mContext as HomeActivity.NavigationControls).onCommentInfo(comment.sender,comment.sender)
                        }
                    }
                })
            notifyDataSetChanged()
        }
    }

    private fun loadUserInfo(holder: Viewholder, sender: String) {
        val reference = Connection.userRef + File.separator + sender
        FirebaseMethods.singleValueEvent(reference, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    holder.comment_username.text = user.username
                    if (user.imageUrl.equals("null")) {
                        holder.comment_pic.setImageResource(R.drawable.google_logo)
                    } else {
                        Glide.with(mContext).load(user.imageUrl).into(holder.comment_pic)
                    }
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        val comment = commentList[position]
        if(comment.isComment){
            return COMMENT_TYPE
        }else{
            return REPLY_TYPE
        }
    }
}