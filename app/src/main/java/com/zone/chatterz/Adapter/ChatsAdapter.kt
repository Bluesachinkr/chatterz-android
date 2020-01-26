package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class ChatsAdapter(context: Context, list: List<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser
    private val MESSAGE_LEFT_RECEIVER = 0
    private val MESSAGE_RIGHT_SENDER = 1
    private var mChat: List<Chat> = list
    private var mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType.equals(MESSAGE_RIGHT_SENDER)) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.chat_message_sending, parent, false)
            return ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext)
                .inflate(R.layout.chat_message_recieving, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChat.get(position)
        holder.textMessage.text = chat.message
        showPhotoImage(chat.sender, holder.profileChatImage)
        if (position == mChat.size - 1) {
            holder.isSeen.visibility = View.VISIBLE
            if (chat.isSeen) {
                holder.isSeen.setImageResource(R.drawable.seen_icon)
            } else {
                holder.isSeen.setImageResource(R.drawable.not_seen_icon)
            }
        } else {
            holder.isSeen.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textMessage: TextView = itemView.findViewById(R.id.message)
        var isSeen: ImageView = itemView.findViewById(R.id.isSeenMessage)
        var profileChatImage: CircularImageView =
            itemView.findViewById(R.id.chatProfileImgReceiving)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (mChat.get(position).sender.equals(firebaseUser.uid)) {
            return MESSAGE_RIGHT_SENDER
        } else {
            return MESSAGE_LEFT_RECEIVER
        }

    }

    private fun showPhotoImage(userid: String, imageView: CircularImageView) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users" + userid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    if (user.id.equals(userid)) {
                        if (user.imageUrl.equals("null")) {
                            imageView.setImageResource(R.drawable.google_logo)
                        } else {
                            Glide.with(mContext).load(user.imageUrl).into(imageView)
                        }
                    }
                }
            }
        })
    }
}