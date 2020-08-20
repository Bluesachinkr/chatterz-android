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
import java.io.File

class ChatRecentAdapter(context: Context, list: List<String>) :
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

        val userId: String = mUsers.get(position)
        FirebaseMethods.singleValueEvent(Connection.userRef+ File.separator+userId,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    holder.userName.text = user.username

                    lastMessage(userId, holder.userlastMessage)

                    if (user.status.equals("online")) {
                        holder.status.visibility = View.VISIBLE
                    } else {
                        holder.status.visibility = View.GONE
                    }
                    if (user.imageUrl.equals("null")) {
                        if(user.gender.equals("Male")){
                           holder.profileImage.setImageResource(R.drawable.ic_male_gender_profile)
                        }else{
                            holder.profileImage.setImageResource(R.drawable.ic_female_gender_profile)
                        }
                    } else {
                        Glide.with(mContext).load(user.imageUrl).into(holder.profileImage)
                    }
                }
            }
        })

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatMessageActivity::class.java)
            intent.putExtra("UserId", userId)
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
        val ref = Connection.userChats+File.separator+Connection.user+File.separator+userId
        FirebaseMethods.addValueEvent(ref, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                for (dataSet in dataSnapshot.children) {
                    val chat = dataSet.getValue(Chat::class.java)
                    chat?.let{
                        if (it.sender.equals(Connection.user) && it.receiver.equals(
                                userId
                            ) ||
                            it.sender.equals(userId) && it.receiver.equals(Connection.user)
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