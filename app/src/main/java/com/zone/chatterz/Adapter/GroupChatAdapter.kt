package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.GroupChats
import com.zone.chatterz.R
import java.text.SimpleDateFormat

class GroupChatAdapter(context : Context,mGroupChat : List<GroupChats>,userId : String) : RecyclerView.Adapter<GroupChatAdapter.ViewHolder>(){

    private val mContext = context
    private val mGrpChat : List<GroupChats> = mGroupChat
    private val MESSAGE_SENDER =1
    private val MESSAGE_RECEIVER =0

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var messageView : TextView = itemView.findViewById(R.id.message_groupChat)
        var timeView : TextView = itemView.findViewById(R.id.time_sending_groupChat)
        var profileImg : CircularImageView = itemView.findViewById(R.id.groupChatImgReceiving)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatAdapter.ViewHolder {
        if(viewType==MESSAGE_SENDER){
            val view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_sending,parent)
            return ViewHolder(view)
        }else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_receiving,parent)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mGrpChat.size
    }

    override fun onBindViewHolder(holder: GroupChatAdapter.ViewHolder, position: Int) {
       val chat = mGrpChat.get(position)
      holder.messageView.text = chat.message
        val sd = SimpleDateFormat("hh:mm")
        holder.timeView.text = sd.format(chat.dateTime)
    }

    override fun getItemViewType(position: Int): Int {
        val user = FirebaseAuth.getInstance().currentUser!!
        if(mGrpChat.get(position).sender.equals(user.uid)){
            return MESSAGE_SENDER
        }else{
            return MESSAGE_RECEIVER
        }
    }
}