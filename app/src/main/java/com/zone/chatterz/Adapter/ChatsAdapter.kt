package com.zone.chatterz.Adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.R

class ChatsAdapter(context: Context, list: List<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private val MESSAGE_LEFT_RECEIVER = 0
    private val MESSAGE_RIGHT_SENDER = 1

    private var mChat: List<Chat> = list
    private var mContext = context

    private lateinit var firebaseUser: FirebaseUser

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
        holder.textMessage.text = chat.isSeen.toString()

        if (position == mChat.size - 1) {
            if(chat.isSeen) {
               holder.isSeen.visibility = View.VISIBLE
                holder.isNotSeen.visibility = View.GONE
            }else{
               holder.isNotSeen.visibility = View.VISIBLE
                holder.isSeen.visibility = View.GONE
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textMessage: TextView = itemView.findViewById(R.id.message)
        var isSeen : ImageView  = itemView.findViewById(R.id.isSeenMessage)
        var isNotSeen : ImageView = itemView.findViewById(R.id.isNotSeenMessage)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        if (mChat.get(position).sender.equals(firebaseUser.uid)) {
            return MESSAGE_RIGHT_SENDER
        } else {
            return MESSAGE_LEFT_RECEIVER
        }

    }
}