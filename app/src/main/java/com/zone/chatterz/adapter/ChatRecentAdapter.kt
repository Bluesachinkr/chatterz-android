package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.singleChat.ChatMessageActivity
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Chat
import com.zone.chatterz.model.User
import com.zone.chatterz.R

class ChatRecentAdapter(context: Context, list: List<User>) :
    RecyclerView.Adapter<ChatRecentAdapter.Viewholder>() {

    private val mUsers = list
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        val user: User = mUsers.get(position)
        holder.userName.text = user.username

        lastMessage(user.id, holder.userlastMessage)

        if (user.status.equals("online")) {
            holder.status.visibility = View.VISIBLE
        } else {
            holder.status.visibility = View.GONE
        }
        if (user.imageUrl.equals("null")) {
            holder.profileImage.setImageResource(R.drawable.google_logo)
        } else {
            Glide.with(mContext).load(user.imageUrl).into(holder.profileImage)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatMessageActivity::class.java)
            intent.putExtra("UserId", user.id)
            mContext.startActivity(intent)
        }

    }


    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName = itemView.findViewById<TextView>(R.id.userName)
        var userlastMessage = itemView.findViewById<TextView>(R.id.messageUser)
        var statusLastOnline = itemView.findViewById<TextView>(R.id.statusLastOnline)
        var status: CircularImageView = itemView.findViewById(R.id.onlinestatus)
        var profileImage = itemView.findViewById<CircularImageView>(R.id.user_profileImage)

    }

    private fun lastMessage(userId: String, lastMessage: TextView) {
        var message: String = "nothing"
        FirebaseMethods.addValueEventChild(Connection.userChats, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                for (dataSet in dataSnapshot.children) {
                    val chat = dataSet.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.sender.equals(FirebaseMethods.firebaseUser.uid) && chat.receiver.equals(
                                userId
                            ) ||
                            chat.sender.equals(userId) && chat.receiver.equals(FirebaseMethods.firebaseUser.uid)
                        ) {
                            message = chat.message
                        }
                    }
                }
                when (message) {
                    "nothing" -> {
                        lastMessage.text = ""
                    }
                    else -> {
                        lastMessage.text = message
                    }
                }
            }
        })
    }
}